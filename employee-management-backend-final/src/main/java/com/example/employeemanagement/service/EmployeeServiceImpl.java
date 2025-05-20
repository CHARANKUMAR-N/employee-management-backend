package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.*;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.*;
import com.example.employeemanagement.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private final EmployeeRepository employeeRepository;
	private final EducationRepository educationRepository;
	private final CertificationRepository certificationRepository;
	private final SkillRepository skillRepository;
	private final DocumentRepository documentRepository;
	private final ModelMapper modelMapper;
	private final ProfilePhotoRepository profilePhotoRepository;
	private final ProfilePhotoService profilePhotoService;
	private final ExperienceRepository experienceRepository;

	@Autowired
	public EmployeeServiceImpl(EmployeeRepository employeeRepository, EducationRepository educationRepository,
			CertificationRepository certificationRepository, SkillRepository skillRepository,
			DocumentRepository documentRepository, ProfilePhotoRepository profilePhotoRepository,
			ModelMapper modelMapper, ProfilePhotoService profilePhotoService,
			ExperienceRepository experienceRepository) {
		this.employeeRepository = employeeRepository;
		this.educationRepository = educationRepository;
		this.certificationRepository = certificationRepository;
		this.skillRepository = skillRepository;
		this.documentRepository = documentRepository;
		this.profilePhotoRepository = profilePhotoRepository;
		this.modelMapper = modelMapper;
		this.profilePhotoService = profilePhotoService;
		this.experienceRepository = experienceRepository;
	}

	@Override
	public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
		validateUniqueConstraints(employeeDTO, null);

		// Ensure profile photo is not set during initial creation
		employeeDTO.setProfilePhoto(null);

		Employee employee = convertToNewEntity(employeeDTO);
		Employee savedEmployee = employeeRepository.save(employee);
		return convertToDTO(savedEmployee);
	}

	@Override
	public EmployeeDTO getEmployeeById(Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

		EmployeeDTO employeeDTO = convertToDTO(employee);

		// Load profile photo if not already loaded
		ProfilePhotoDTO photoDTO = profilePhotoService.getProfilePhoto(id);
		employeeDTO.setProfilePhoto(photoDTO);

		return employeeDTO;
	}

	@Override
	public List<EmployeeDTO> getAllEmployees() {
		return employeeRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
		Employee existingEmployee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

		// Handle document deletions
		if (employeeDTO.getDocumentsToDelete() != null && !employeeDTO.getDocumentsToDelete().isEmpty()) {
			List<Document> toDelete = documentRepository.findAllById(employeeDTO.getDocumentsToDelete());
			documentRepository.deleteAll(toDelete);
		}

		// Handle profile photo removal
		if (employeeDTO.getRemoveProfilePhoto() != null && employeeDTO.getRemoveProfilePhoto()) {
			if (existingEmployee.getProfilePhoto() != null) {
				profilePhotoRepository.delete(existingEmployee.getProfilePhoto());
				existingEmployee.setProfilePhoto(null);
			}
			employeeDTO.setProfilePhoto(null);
		}
		// If no photo data is provided and we're not removing, keep existing photo
		else if (employeeDTO.getProfilePhoto() == null
				|| (employeeDTO.getProfilePhoto() != null && !employeeDTO.getProfilePhoto().hasPhotoData())) {
			employeeDTO.setProfilePhoto(convertToDTO(existingEmployee).getProfilePhoto());
		}

		// Validate unique constraints before updating
		validateUniqueConstraints(employeeDTO, existingEmployee);

		// Map other fields while preserving the ID
		employeeDTO.setEmployeeId(id);
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.map(employeeDTO, existingEmployee);

		// Update relationships
		updateEmployeeRelationships(existingEmployee, employeeDTO);

		Employee updatedEmployee = employeeRepository.save(existingEmployee);
		return convertToDTO(updatedEmployee);
	}

	private void updateProfilePhoto(Employee employee, ProfilePhotoDTO photoDTO) {
		if (employee.getProfilePhoto() == null) {
			// Create new photo
			ProfilePhoto newPhoto = modelMapper.map(photoDTO, ProfilePhoto.class);
			newPhoto.setEmployee(employee);
			employee.setProfilePhoto(newPhoto);
		} else {
			// Update existing photo without changing ID
			ProfilePhoto existingPhoto = employee.getProfilePhoto();
			existingPhoto.setFileName(photoDTO.getFileName());
			existingPhoto.setFileType(photoDTO.getFileType());
			existingPhoto.setFileSize(photoDTO.getFileSize());
			existingPhoto.setData(photoDTO.getData());
		}
	}

	@Override
	public void deleteEmployee(Long id) {
		educationRepository.deleteByEmployeeEmployeeId(id);
		certificationRepository.deleteByEmployeeEmployeeId(id);
		skillRepository.deleteByEmployeeEmployeeId(id);
		documentRepository.deleteByEmployeeEmployeeId(id);
		employeeRepository.deleteById(id);
	}

	private void validateUniqueConstraints(EmployeeDTO employeeDTO, Employee existingEmployee) {
		if (existingEmployee == null || !existingEmployee.getEmail().equals(employeeDTO.getEmail())) {
			if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
				throw new IllegalArgumentException("Email already exists");
			}
		}

		if (existingEmployee == null || !existingEmployee.getPersonalEmail().equals(employeeDTO.getPersonalEmail())) {
			if (employeeRepository.existsByPersonalEmail(employeeDTO.getPersonalEmail())) {
				throw new IllegalArgumentException("Personal email already exists");
			}
		}

		if (existingEmployee == null || !existingEmployee.getMobile().equals(employeeDTO.getMobile())) {
			if (employeeRepository.existsByMobile(employeeDTO.getMobile())) {
				throw new IllegalArgumentException("Mobile number already exists");
			}
		}
	}

	private void updateEmployeeRelationships(Employee employee, EmployeeDTO employeeDTO) {
		updateEducations(employee,
				employeeDTO.getEducationList() != null ? employeeDTO.getEducationList() : new ArrayList<>(),
				educationRepository.findByEmployeeEmployeeId(employee.getEmployeeId()));

		updateCertifications(employee,
				employeeDTO.getCertifications() != null ? employeeDTO.getCertifications() : new ArrayList<>(),
				certificationRepository.findByEmployeeEmployeeId(employee.getEmployeeId()));

		updateSkills(employee, employeeDTO.getSkills() != null ? employeeDTO.getSkills() : new ArrayList<>(),
				skillRepository.findByEmployeeEmployeeId(employee.getEmployeeId()));

		updateDocuments(employee, employeeDTO.getDocuments() != null ? employeeDTO.getDocuments() : new ArrayList<>(),
				documentRepository.findByEmployeeEmployeeId(employee.getEmployeeId()));
	}

	private void updateEducations(Employee employee, List<EducationDTO> educationDTOs,
			List<Education> currentEducations) {
		employee.getEducationList().clear();
		Set<Long> currentIds = currentEducations.stream().map(Education::getEducationId).collect(Collectors.toSet());

		for (EducationDTO dto : educationDTOs) {
			Education education;
			if (dto.getEducationId() != null && currentIds.contains(dto.getEducationId())) {
				education = currentEducations.stream().filter(e -> e.getEducationId().equals(dto.getEducationId()))
						.findFirst().orElseThrow(() -> new ResourceNotFoundException(
								"Education not found with id: " + dto.getEducationId()));
				modelMapper.map(dto, education);
			} else {
				education = modelMapper.map(dto, Education.class);
				education.setEducationId(null);
				education.setVersion(0L);
			}
			education.setEmployee(employee);
			employee.getEducationList().add(education);
		}

		Set<Long> dtoIds = educationDTOs.stream().map(EducationDTO::getEducationId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<Education> toRemove = currentEducations.stream().filter(e -> !dtoIds.contains(e.getEducationId()))
				.collect(Collectors.toList());

		educationRepository.deleteAll(toRemove);
	}

	private void updateCertifications(Employee employee, List<CertificationDTO> certificationDTOs,
			List<Certification> currentCerts) {
		employee.getCertifications().clear();
		Set<Long> currentIds = currentCerts.stream().map(Certification::getCertificationId).collect(Collectors.toSet());

		for (CertificationDTO dto : certificationDTOs) {
			Certification certification;
			if (dto.getCertificationId() != null && currentIds.contains(dto.getCertificationId())) {
				certification = currentCerts.stream()
						.filter(c -> c.getCertificationId().equals(dto.getCertificationId())).findFirst()
						.orElseThrow(() -> new ResourceNotFoundException(
								"Certification not found with id: " + dto.getCertificationId()));
				modelMapper.map(dto, certification);
			} else {
				certification = modelMapper.map(dto, Certification.class);
				certification.setCertificationId(null);
				certification.setVersion(0L);
			}
			certification.setEmployee(employee);
			employee.getCertifications().add(certification);
		}

		Set<Long> dtoIds = certificationDTOs.stream().map(CertificationDTO::getCertificationId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<Certification> toRemove = currentCerts.stream().filter(c -> !dtoIds.contains(c.getCertificationId()))
				.collect(Collectors.toList());

		certificationRepository.deleteAll(toRemove);
	}

	private void updateSkills(Employee employee, List<SkillDTO> skillDTOs, List<Skill> currentSkills) {
		employee.getSkills().clear();
		Set<Long> currentIds = currentSkills.stream().map(Skill::getSkillId).collect(Collectors.toSet());

		for (SkillDTO dto : skillDTOs) {
			Skill skill;
			if (dto.getSkillId() != null && currentIds.contains(dto.getSkillId())) {
				skill = currentSkills.stream().filter(s -> s.getSkillId().equals(dto.getSkillId())).findFirst()
						.orElseThrow(
								() -> new ResourceNotFoundException("Skill not found with id: " + dto.getSkillId()));
				modelMapper.map(dto, skill);
			} else {
				skill = modelMapper.map(dto, Skill.class);
				skill.setSkillId(null);
				skill.setVersion(0L);
			}
			skill.setEmployee(employee);
			employee.getSkills().add(skill);
		}

		Set<Long> dtoIds = skillDTOs.stream().map(SkillDTO::getSkillId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<Skill> toRemove = currentSkills.stream().filter(s -> !dtoIds.contains(s.getSkillId()))
				.collect(Collectors.toList());

		skillRepository.deleteAll(toRemove);
	}

	private void updateDocuments(Employee employee, List<DocumentDTO> documentDTOs, List<Document> currentDocuments) {
		// Only update metadata for existing documents
		Set<Long> currentIds = currentDocuments.stream().map(Document::getDocumentId).collect(Collectors.toSet());

		for (DocumentDTO dto : documentDTOs) {
			if (dto.getDocumentId() != null && currentIds.contains(dto.getDocumentId())) {
				Document existingDoc = currentDocuments.stream()
						.filter(d -> d.getDocumentId().equals(dto.getDocumentId())).findFirst()
						.orElseThrow(() -> new ResourceNotFoundException(
								"Document not found with id: " + dto.getDocumentId()));

				existingDoc.setDocumentType(dto.getDocumentType());
				existingDoc.setVersion(dto.getVersion());
			}
		}
	}

	private void updateExperiences(Employee employee, List<ExperienceDTO> experienceDTOs,
			List<Experience> currentExperiences) {
		employee.getExperiences().clear();
		Set<Long> currentIds = currentExperiences.stream().map(Experience::getExperienceId).collect(Collectors.toSet());

		for (ExperienceDTO dto : experienceDTOs) {
			Experience experience;
			if (dto.getExperienceId() != null && currentIds.contains(dto.getExperienceId())) {
				experience = currentExperiences.stream().filter(e -> e.getExperienceId().equals(dto.getExperienceId()))
						.findFirst().orElseThrow(() -> new ResourceNotFoundException(
								"Experience not found with id: " + dto.getExperienceId()));
				modelMapper.map(dto, experience);
			} else {
				experience = modelMapper.map(dto, Experience.class);
				experience.setExperienceId(null);
				experience.setVersion(0L);
			}
			experience.setEmployee(employee);
			employee.getExperiences().add(experience);
		}

		Set<Long> dtoIds = experienceDTOs.stream().map(ExperienceDTO::getExperienceId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<Experience> toRemove = currentExperiences.stream().filter(e -> !dtoIds.contains(e.getExperienceId()))
				.collect(Collectors.toList());

		experienceRepository.deleteAll(toRemove);
	}

	private Employee convertToNewEntity(EmployeeDTO employeeDTO) {
		Employee employee = modelMapper.map(employeeDTO, Employee.class);
		employee.setEducationList(new ArrayList<>());
		employee.setCertifications(new ArrayList<>());
		employee.setSkills(new ArrayList<>());
		employee.setDocuments(new ArrayList<>());

		if (employeeDTO.getEducationList() != null) {
			employeeDTO.getEducationList().forEach(eduDto -> {
				Education education = modelMapper.map(eduDto, Education.class);
				education.setEducationId(null);
				education.setVersion(0L);
				education.setEmployee(employee);
				employee.getEducationList().add(education);
			});
		}

		if (employeeDTO.getCertifications() != null) {
			employeeDTO.getCertifications().forEach(certDto -> {
				Certification certification = modelMapper.map(certDto, Certification.class);
				certification.setCertificationId(null);
				certification.setVersion(0L);
				certification.setEmployee(employee);
				employee.getCertifications().add(certification);
			});
		}

		if (employeeDTO.getSkills() != null) {
			employeeDTO.getSkills().forEach(skillDto -> {
				Skill skill = modelMapper.map(skillDto, Skill.class);
				skill.setSkillId(null);
				skill.setVersion(0L);
				skill.setEmployee(employee);
				employee.getSkills().add(skill);
			});
		}

		if (employeeDTO.getExperiences() != null) {
			employeeDTO.getExperiences().forEach(expDto -> {
				Experience experience = modelMapper.map(expDto, Experience.class);
				experience.setExperienceId(null);
				experience.setVersion(0L);
				experience.setEmployee(employee);
				employee.getExperiences().add(experience);
			});
		}

		if (employeeDTO.getDocuments() != null) {
			employeeDTO.getDocuments().forEach(docDto -> {
				Document document = modelMapper.map(docDto, Document.class);
				document.setDocumentId(null);
				document.setVersion(0L);
				document.setEmployee(employee);
				employee.getDocuments().add(document);
			});
		}

		return employee;
	}

	private EmployeeDTO convertToDTO(Employee employee) {
		EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

		if (employee.getProfilePhoto() != null) {
			ProfilePhotoDTO photoDTO = new ProfilePhotoDTO();
			photoDTO.setId(employee.getProfilePhoto().getId());
			photoDTO.setFileName(employee.getProfilePhoto().getFileName());
			photoDTO.setFileType(employee.getProfilePhoto().getFileType());
			photoDTO.setFileSize(employee.getProfilePhoto().getFileSize());
			photoDTO.setData(employee.getProfilePhoto().getData());
			photoDTO.setEmployeeId(employee.getEmployeeId());
			employeeDTO.setProfilePhoto(photoDTO);
		}

		employeeDTO.setEducationList(employee.getEducationList().stream().map(edu -> {
			EducationDTO dto = modelMapper.map(edu, EducationDTO.class);
			dto.setVersion(edu.getVersion());
			return dto;
		}).collect(Collectors.toList()));

		employeeDTO.setCertifications(employee.getCertifications().stream().map(cert -> {
			CertificationDTO dto = modelMapper.map(cert, CertificationDTO.class);
			dto.setVersion(cert.getVersion());
			return dto;
		}).collect(Collectors.toList()));

		employeeDTO.setSkills(employee.getSkills().stream().map(skill -> {
			SkillDTO dto = modelMapper.map(skill, SkillDTO.class);
			dto.setVersion(skill.getVersion());
			return dto;
		}).collect(Collectors.toList()));

		employeeDTO.setDocuments(employee.getDocuments().stream().map(doc -> {
			DocumentDTO dto = modelMapper.map(doc, DocumentDTO.class);
			dto.setEmployeeId(employee.getEmployeeId());
			return dto;
		}).collect(Collectors.toList()));

		employeeDTO.setExperiences(employee.getExperiences().stream().map(exp -> {
			ExperienceDTO dto = modelMapper.map(exp, ExperienceDTO.class);
			dto.setVersion(exp.getVersion());
			return dto;
		}).collect(Collectors.toList()));

		return employeeDTO;
	}

	@Override
	public EmployeeDTO getEmployeeByEmail(String email) {
		// First try to find by email
		logger.debug("Fetching employee with email: {}", email);
		Optional<Employee> employee = employeeRepository.findByEmail(email);

		// If not found, try personal email
		if (employee.isEmpty()) {
			employee = employeeRepository.findByPersonalEmail(email);
		}

		// If still not found, try to extract email from Auth0 ID if it's in that format
		if (employee.isEmpty() && email.startsWith("auth0|")) {
			// Try to find by email without the Auth0 prefix
			String cleanEmail = email.substring(email.indexOf("|") + 1);
			employee = employeeRepository.findByEmail(cleanEmail);
			if (employee.isEmpty()) {
				employee = employeeRepository.findByPersonalEmail(cleanEmail);
			}
		}

		return employee.map(this::convertToDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with identifier: " + email));
	}

}