package com.app.caresync.dto;

import lombok.Data;

@Data
public class JwtResponse {
	
	private String token;
	private String type = "Bearer";
	private String email;
	private String role;
	private String fullName;
	private String phone;
	
	public JwtResponse(String accessToken, String email, String role, String fullName, String phone) {
		this.token = accessToken;
		this.email = email;
		this.role = role;
		this.fullName = fullName;
		this.phone = phone;
	}
}
