package com.app.caresync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.caresync.dto.PasswordChangeRequest;
import com.app.caresync.service.AuthService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private AuthService authService;
	
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(Authentication authentication,
			@RequestBody PasswordChangeRequest request) {
		
		String status = authService.changePassword(authentication.getName(), request);
		
		return ResponseEntity.ok(status);
	}
	
	@PutMapping("/deactivate")
	public ResponseEntity<?> deactivateUser(Authentication authentication) {
		String status = authService.deactivateUser(authentication.getName());
		
		return ResponseEntity.ok(status);
	}
}
