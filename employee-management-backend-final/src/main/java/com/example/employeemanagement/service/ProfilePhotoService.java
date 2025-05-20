package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.ProfilePhotoDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.ProfilePhoto;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.ProfilePhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class ProfilePhotoService {

	private final ProfilePhotoRepository profilePhotoRepository;
	private final EmployeeRepository employeeRepository;

	@Autowired
	public ProfilePhotoService(ProfilePhotoRepository profilePhotoRepository, EmployeeRepository employeeRepository) {
		this.profilePhotoRepository = profilePhotoRepository;
		this.employeeRepository = employeeRepository;
	}

	@Transactional
	public ProfilePhotoDTO uploadProfilePhoto(Long employeeId, MultipartFile file) throws IOException {
	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

	    // Delete any existing photo first
	    deleteProfilePhoto(employeeId);

	    // Validate file
	    if (file == null || file.isEmpty()) {
	        throw new IllegalArgumentException("File cannot be empty");
	    }

	    // Create new photo
	    ProfilePhoto profilePhoto = new ProfilePhoto();
	    profilePhoto.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
	    profilePhoto.setFileType(file.getContentType());
	    profilePhoto.setFileSize(file.getSize());
	    profilePhoto.setData(file.getBytes());
	    profilePhoto.setEmployee(employee);

	    ProfilePhoto savedPhoto = profilePhotoRepository.save(profilePhoto);
	    return convertToDTO(savedPhoto);
	}
	
	public ProfilePhotoDTO getProfilePhoto(Long employeeId) {
		ProfilePhoto profilePhoto = profilePhotoRepository.findByEmployee_EmployeeId(employeeId).orElse(null);

		if (profilePhoto == null) {
			return null;
		}
		return convertToDTO(profilePhoto);
	}

	@Transactional
	public void deleteProfilePhoto(Long employeeId) {
		profilePhotoRepository.deleteByEmployeeId(employeeId);
	}

	private ProfilePhotoDTO convertToDTO(ProfilePhoto profilePhoto) {
		ProfilePhotoDTO dto = new ProfilePhotoDTO();
		dto.setId(profilePhoto.getId());
		dto.setFileName(profilePhoto.getFileName());
		dto.setFileType(profilePhoto.getFileType());
		dto.setFileSize(profilePhoto.getFileSize());
		dto.setData(profilePhoto.getData());
		dto.setEmployeeId(profilePhoto.getEmployee().getEmployeeId());
		return dto;
	}
}
