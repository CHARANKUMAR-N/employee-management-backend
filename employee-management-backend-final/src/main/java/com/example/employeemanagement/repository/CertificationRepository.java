package com.example.employeemanagement.repository;
 
import com.example.employeemanagement.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByEmployeeEmployeeId(Long employeeId);
    void deleteByEmployeeEmployeeId(Long employeeId);
}
