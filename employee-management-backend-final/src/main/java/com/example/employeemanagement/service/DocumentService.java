package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
	List<DocumentDTO> getDocumentsByEmployeeId(Long employeeId);
	DocumentDTO getDocumentById(Long documentId);
	DocumentDTO uploadDocument(Long employeeId, MultipartFile file, String documentType);
	void deleteDocument(Long documentId);
	byte[] downloadDocument(Long documentId);
}