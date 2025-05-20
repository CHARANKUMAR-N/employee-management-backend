package com.example.employeemanagement.dto;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDate;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private Long certificationId;
    private Long version;

    
    @NotBlank(message = "Certification name is required")
    private String name;
    
    @NotBlank(message = "Organization is required")
    private String organization;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
}

