package com.example.employeemanagement.service;
 
import com.example.employeemanagement.dto.EmployeeDTO;

import java.util.List;
 
public interface EmployeeService {
    EmployeeDTO saveEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
	void deleteEmployee(Long id);
	EmployeeDTO getEmployeeByEmail(String subject);
}