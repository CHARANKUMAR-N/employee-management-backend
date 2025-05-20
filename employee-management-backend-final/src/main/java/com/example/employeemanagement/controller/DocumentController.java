package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.DocumentDTO;
import com.example.employeemanagement.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/employees/{employeeId}/documents")
public class DocumentController {
	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@GetMapping
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<List<DocumentDTO>> getEmployeeDocuments(@PathVariable Long employeeId) {
		List<DocumentDTO> documents = documentService.getDocumentsByEmployeeId(employeeId);
		return ResponseEntity.ok(documents);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<DocumentDTO> uploadDocument(@PathVariable Long employeeId,
			@RequestParam("file") MultipartFile file, @RequestParam("documentType") String documentType) {
		try {
			if (file.isEmpty()) {
				throw new IllegalArgumentException("File cannot be empty");
			}

			// Validate file size
			if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
				throw new IllegalArgumentException("File size exceeds 5MB limit");
			}

			// Validate file type
			String contentType = file.getContentType();
			if (!isValidDocumentType(contentType)) {
				throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, DOCX are allowed");
			}

			DocumentDTO documentDTO = documentService.uploadDocument(employeeId, file, documentType);
			return ResponseEntity.ok(documentDTO);
		} catch (Exception e) {
			throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
		}
	}

	private boolean isValidDocumentType(String contentType) {
		return contentType != null && (contentType.equals("application/pdf") || contentType.equals("application/msword")
				|| contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
	}

	@GetMapping("/{documentId}")
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<byte[]> downloadDocument(@PathVariable Long employeeId, @PathVariable Long documentId) {
		DocumentDTO documentDTO = documentService.getDocumentById(documentId);
		byte[] data = documentService.downloadDocument(documentId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(documentDTO.getFileType()));
		headers.setContentLength(documentDTO.getFileSize());
		headers.setContentDispositionFormData("attachment", documentDTO.getFileName());

		return new ResponseEntity<>(data, headers, HttpStatus.OK);
	}

	@DeleteMapping("/{documentId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> deleteDocument(@PathVariable Long employeeId, @PathVariable Long documentId) {
		documentService.deleteDocument(documentId);
		return ResponseEntity.noContent().build();
	}

}