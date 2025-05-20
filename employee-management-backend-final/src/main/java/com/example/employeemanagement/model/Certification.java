package com.example.employeemanagement.model;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDate;
 
@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String organization;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Version
    @Column(nullable = false)
    private Long version = 0L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
