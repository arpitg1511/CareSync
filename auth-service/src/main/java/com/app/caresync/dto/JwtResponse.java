package com.app.caresync.dto;

import lombok.Data;

@Data
public class JwtResponse {
	
	private String token;
	private String type = "Bearer";
	private String email;
	private String role;
	
	public JwtResponse(String accessToken, String email, String role) {
		this.token = accessToken;
		this.email = email;
		this.role = role;
	}
}
