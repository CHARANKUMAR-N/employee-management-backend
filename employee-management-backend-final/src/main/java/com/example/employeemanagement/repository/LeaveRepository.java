package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Leave;
import com.example.employeemanagement.model.Leave.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
	List<Leave> findByEmployeeEmployeeId(Long employeeId);

	List<Leave> findByStatus(LeaveStatus status);

	List<Leave> findByEmployeeEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

	@Query("SELECT l FROM Leave l WHERE l.startDate <= :endDate AND l.endDate >= :startDate AND l.status = 'APPROVED'")
	List<Leave> findOverlappingApprovedLeaves(LocalDate startDate, LocalDate endDate);

	@Query("SELECT COUNT(l) FROM Leave l WHERE l.status = 'PENDING'")
	long countPendingLeaves();
}