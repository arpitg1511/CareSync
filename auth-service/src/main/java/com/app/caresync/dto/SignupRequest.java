package com.app.caresync.dto;

import lombok.Data;

@Data
public class SignupRequest {
	
	private String fullName;
	private String email;
	private String password;
	private String phone;
	private String role;
	private String speciality;
}
