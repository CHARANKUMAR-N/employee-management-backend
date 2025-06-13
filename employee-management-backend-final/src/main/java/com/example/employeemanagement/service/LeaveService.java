package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.LeaveDTO;
import com.example.employeemanagement.model.Leave.LeaveStatus;
import java.util.List;

public interface LeaveService {
    LeaveDTO applyLeave(LeaveDTO leaveDTO);
    LeaveDTO updateLeaveStatus(Long leaveId, LeaveStatus status, String reason);
    List<LeaveDTO> getLeavesByEmployeeId(Long employeeId);
    List<LeaveDTO> getPendingLeaves();
    long countPendingLeaves();
    LeaveDTO getLeaveById(Long leaveId);
    void cancelLeave(Long leaveId);
}