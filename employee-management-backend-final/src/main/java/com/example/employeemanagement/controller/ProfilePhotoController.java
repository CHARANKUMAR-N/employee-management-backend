package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.ProfilePhotoDTO;
import com.example.employeemanagement.service.ProfilePhotoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/employees/{employeeId}/profile-photo")

public class ProfilePhotoController {
	private final ProfilePhotoService profilePhotoService;

	public ProfilePhotoController(ProfilePhotoService profilePhotoService) {
		this.profilePhotoService = profilePhotoService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<ProfilePhotoDTO> uploadProfilePhoto(@PathVariable Long employeeId,
			@RequestParam("file") MultipartFile file) throws IOException {
		ProfilePhotoDTO profilePhotoDTO = profilePhotoService.uploadProfilePhoto(employeeId, file);
		return ResponseEntity.ok(profilePhotoDTO);
	}

	@GetMapping
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<byte[]> getProfilePhoto(@PathVariable Long employeeId) {
		ProfilePhotoDTO profilePhotoDTO = profilePhotoService.getProfilePhoto(employeeId);
		if (profilePhotoDTO == null || profilePhotoDTO.getData() == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(profilePhotoDTO.getFileType()));
		headers.setContentLength(profilePhotoDTO.getFileSize());
		headers.setContentDispositionFormData("inline", profilePhotoDTO.getFileName());
		headers.set("X-Photo-Id", profilePhotoDTO.getId().toString());
		headers.set("X-File-Name", profilePhotoDTO.getFileName());
		headers.set("X-File-Type", profilePhotoDTO.getFileType());
		headers.set("X-File-Size", profilePhotoDTO.getFileSize().toString());

		return new ResponseEntity<>(profilePhotoDTO.getData(), headers, HttpStatus.OK);
	}

	@DeleteMapping
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> deleteProfilePhoto(@PathVariable Long employeeId) {
		profilePhotoService.deleteProfilePhoto(employeeId);
		return ResponseEntity.noContent().build();
	}

}
