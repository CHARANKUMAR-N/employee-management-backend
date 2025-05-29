package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByProjectId(Long projectId);
    @Query("SELECT t FROM Team t WHERE t.projectManager.id = :projectManagerId")
    List<Team> findByProjectManagerId(@Param("projectManagerId") Long projectManagerId);
    
    @Query("SELECT t FROM Team t WHERE t.teamManager.id = :teamManagerId")
    List<Team> findByTeamManagerId(@Param("teamManagerId") Long teamManagerId);

}
