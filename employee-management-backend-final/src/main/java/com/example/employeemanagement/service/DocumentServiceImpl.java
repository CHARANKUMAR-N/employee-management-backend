package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DocumentDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Document;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.DocumentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository documentRepository;
	private final EmployeeRepository employeeRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public DocumentServiceImpl(DocumentRepository documentRepository, EmployeeRepository employeeRepository,
			ModelMapper modelMapper) {
		this.documentRepository = documentRepository;
		this.employeeRepository = employeeRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public List<DocumentDTO> getDocumentsByEmployeeId(Long employeeId) {
		List<Document> documents = documentRepository.findByEmployeeEmployeeId(employeeId);
		return documents.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public DocumentDTO getDocumentById(Long documentId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
		return convertToDTO(document);
	}

	@Override
	public DocumentDTO uploadDocument(Long employeeId, MultipartFile file, String documentType) {
		try {
			Employee employee = employeeRepository.findById(employeeId)
					.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

			Document document = new Document();
			document.setFileName(file.getOriginalFilename());
			document.setFileType(file.getContentType());
			document.setFileSize(file.getSize());
			document.setData(file.getBytes());
			document.setDocumentType(documentType);
			document.setEmployee(employee);

			Document savedDocument = documentRepository.save(document);

			// Explicitly add to employee's documents list
			employee.getDocuments().add(savedDocument);
			employeeRepository.save(employee);

			return convertToDTO(savedDocument);
		} catch (IOException e) {
			throw new RuntimeException("Failed to process document upload", e);
		}
	}

	@Override
	public void deleteDocument(Long documentId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

		// Explicitly remove from employee's documents list
		Employee employee = document.getEmployee();
		if (employee != null) {
			employee.getDocuments().remove(document);
			employeeRepository.save(employee);
		}

		documentRepository.delete(document);
	}

	@Override
	public byte[] downloadDocument(Long documentId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
		return document.getData();
	}

	private DocumentDTO convertToDTO(Document document) {
		DocumentDTO dto = modelMapper.map(document, DocumentDTO.class);
		dto.setEmployeeId(document.getEmployee().getEmployeeId());
		return dto;
	}
}
