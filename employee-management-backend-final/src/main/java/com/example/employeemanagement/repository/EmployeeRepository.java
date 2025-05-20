package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	boolean existsByEmail(String email);
	boolean existsByPersonalEmail(String personalEmail);
	boolean existsByMobile(String mobile);
	@Query("SELECT e FROM Employee e WHERE e.email = :email OR e.personalEmail = :email")
	Optional<Employee> findByEmail(@Param("email") String email);
	Optional<Employee> findByPersonalEmail(String email);
}
