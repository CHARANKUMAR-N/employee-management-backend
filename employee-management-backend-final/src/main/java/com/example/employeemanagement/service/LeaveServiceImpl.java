package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.LeaveDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Employee; // Import Employee
import com.example.employeemanagement.model.Leave;
import com.example.employeemanagement.model.Leave.LeaveStatus;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.LeaveRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

	private final LeaveRepository leaveRepository;
	private final EmployeeRepository employeeRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public LeaveServiceImpl(LeaveRepository leaveRepository, EmployeeRepository employeeRepository,
			ModelMapper modelMapper) {
		this.leaveRepository = leaveRepository;
		this.employeeRepository = employeeRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public LeaveDTO applyLeave(LeaveDTO leaveDTO) {
		validateLeaveRequest(leaveDTO); // This method already confirms employeeId existence

		// --- START OF MODIFICATION ---
		// Fetch the Employee entity using the employeeId from the DTO
		Employee employee = employeeRepository.findById(leaveDTO.getEmployeeId()).orElseThrow(
				() -> new ResourceNotFoundException("Employee not found with id: " + leaveDTO.getEmployeeId()));

		Leave leave = modelMapper.map(leaveDTO, Leave.class);
		// Set the fetched Employee object on the Leave entity
		leave.setEmployee(employee);
		// --- END OF MODIFICATION ---

		leave.setStatus(LeaveStatus.PENDING);
		Leave savedLeave = leaveRepository.save(leave);
		return convertToDTO(savedLeave);
	}

	private void validateLeaveRequest(LeaveDTO leaveDTO) {
		if (leaveDTO.getStartDate().isAfter(leaveDTO.getEndDate())) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}

		// This check is good as it confirms the employee exists
		employeeRepository.findById(leaveDTO.getEmployeeId()).orElseThrow(
				() -> new ResourceNotFoundException("Employee not found with id: " + leaveDTO.getEmployeeId()));

		List<Leave> overlappingLeaves = leaveRepository.findOverlappingApprovedLeaves(leaveDTO.getStartDate(),
				leaveDTO.getEndDate());

		if (!overlappingLeaves.isEmpty()) {
			throw new IllegalArgumentException("There are already approved leaves for this date range");
		}
	}

	@Override
	public LeaveDTO updateLeaveStatus(Long leaveId, LeaveStatus status, String reason) {
		Leave leave = leaveRepository.findById(leaveId)
				.orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

		leave.setStatus(status);
		if (reason != null && !reason.isBlank()) {
			leave.setReason(reason);
		}
		Leave updatedLeave = leaveRepository.save(leave);
		return convertToDTO(updatedLeave);
	}

	@Override
	public List<LeaveDTO> getLeavesByEmployeeId(Long employeeId) {
		return leaveRepository.findByEmployeeEmployeeId(employeeId).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<LeaveDTO> getPendingLeaves() {
		return leaveRepository.findByStatus(LeaveStatus.PENDING).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public long countPendingLeaves() {
		return leaveRepository.countPendingLeaves();
	}

	@Override
	public LeaveDTO getLeaveById(Long leaveId) {
		Leave leave = leaveRepository.findById(leaveId)
				.orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));
		return convertToDTO(leave);
	}

	@Override
	public void cancelLeave(Long leaveId) {
		Leave leave = leaveRepository.findById(leaveId)
				.orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

		if (leave.getStatus() != LeaveStatus.PENDING) {
			throw new IllegalStateException("Only pending leaves can be cancelled");
		}

		leave.setStatus(LeaveStatus.CANCELLED);
		leaveRepository.save(leave);
	}

	private LeaveDTO convertToDTO(Leave leave) {
		LeaveDTO leaveDTO = modelMapper.map(leave, LeaveDTO.class);
		// Ensure employee is not null before accessing its ID/name
		if (leave.getEmployee() != null) {
			leaveDTO.setEmployeeId(leave.getEmployee().getEmployeeId());
			leaveDTO.setEmployeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName());
		}
		return leaveDTO;
	}
}