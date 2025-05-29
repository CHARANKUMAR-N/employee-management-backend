package com.example.employeemanagement.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private Long projectId;
    private String projectName;
    private Long projectManagerId;
    private String projectManagerName;
    private Long teamManagerId;
    private String teamManagerName;
    private List<EmployeeDTO> members;

}
