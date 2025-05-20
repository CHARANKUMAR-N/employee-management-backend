package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByEmployeeEmployeeId(Long employeeId);
    void deleteByEmployeeEmployeeId(Long employeeId);
}
