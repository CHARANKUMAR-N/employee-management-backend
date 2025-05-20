package com.example.employeemanagement.model;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long educationId;
    
    @Column(name = "education_name", nullable = false)
    private String educationName;
    
    @Column(nullable = false)
    private String college;
    
    @Column(nullable = false)
    private String year;
    
    @Column(nullable = false)
    private String percentage;
    
    @Version
    @Column(nullable = false)
    private Long version = 0L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
