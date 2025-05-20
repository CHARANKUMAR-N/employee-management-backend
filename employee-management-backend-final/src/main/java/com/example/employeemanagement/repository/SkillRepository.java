package com.example.employeemanagement.repository;
 
import com.example.employeemanagement.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByEmployeeEmployeeId(Long employeeId);
    void deleteByEmployeeEmployeeId(Long employeeId);
}

