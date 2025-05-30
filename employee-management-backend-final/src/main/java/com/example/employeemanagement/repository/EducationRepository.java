package com.example.employeemanagement.repository;
 
import com.example.employeemanagement.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByEmployeeEmployeeId(Long employeeId);
    void deleteByEmployeeEmployeeId(Long employeeId);
}
