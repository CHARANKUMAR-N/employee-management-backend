package com.example.employeemanagement.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long documentId;

	private String fileName;
	private String fileType;
	private Long fileSize;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.BINARY)
	private byte[] data;

	private String documentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@Version
	private Long version;
}
