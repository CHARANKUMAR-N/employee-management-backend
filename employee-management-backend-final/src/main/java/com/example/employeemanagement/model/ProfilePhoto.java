package com.example.employeemanagement.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profile_photos")
@Getter
@Setter
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "BYTEA", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", unique = true, nullable = false)
    private Employee employee;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (this.employee == null) {
            throw new IllegalStateException("ProfilePhoto must be associated with an Employee");
        }
    }
}
