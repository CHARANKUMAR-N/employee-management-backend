package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    
    // Method to find by employee ID
    Optional<ProfilePhoto> findByEmployee_EmployeeId(Long employeeId);
    
    // Method to delete by employee ID
    @Modifying
    @Query("DELETE FROM ProfilePhoto p WHERE p.employee.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);
}