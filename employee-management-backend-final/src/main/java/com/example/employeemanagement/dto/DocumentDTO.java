package com.example.employeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long documentId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private byte[] data;
    private String documentType;
    private Long employeeId;
    private Long version;
}
