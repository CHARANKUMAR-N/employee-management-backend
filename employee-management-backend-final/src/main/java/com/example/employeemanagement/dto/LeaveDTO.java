package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.Leave.LeaveStatus;
import com.example.employeemanagement.model.Leave.LeaveType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveDTO {
    private Long leaveId;
    private Long employeeId;
    private String employeeName;
    private LeaveType leaveType;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}