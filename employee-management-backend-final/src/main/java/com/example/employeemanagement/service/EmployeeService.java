package com.example.employeemanagement.service;
 
import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.Role;

import java.util.List;
 
public interface EmployeeService {
    EmployeeDTO saveEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
	void deleteEmployee(Long id);
	EmployeeDTO getEmployeeByEmail(String subject);
	List<EmployeeDTO> getEmployeesByProject(Long projectId);
	List<EmployeeDTO> getEmployeesByProjectManager(Long projectManagerId);
	List<EmployeeDTO> getEmployeesByTeamManager(Long teamManagerId);
	List<EmployeeDTO> getEmployeesByRole(Role role);

}
