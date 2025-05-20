package com.example.employeemanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePhotoDTO {
	private Long id;
	private String fileName;
	private String fileType;
	private Long fileSize;
	@JsonIgnore
	private byte[] data;
	private Long employeeId;

	public boolean hasPhotoData() {
		return this.data != null && this.data.length > 0;
	}

}