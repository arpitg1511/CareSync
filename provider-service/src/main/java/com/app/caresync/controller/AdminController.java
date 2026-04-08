package com.app.caresync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.caresync.model.ProviderStatus;
import com.app.caresync.service.ProviderService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	private ProviderService providerService;
	
	@GetMapping("/providers/pending")
	public ResponseEntity<?> getPending() {
		return ResponseEntity.ok(providerService.getPendingProviders());
	}
	
	@PutMapping("/providers/{id}/status")
	public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam ProviderStatus status) {
		return ResponseEntity.ok(providerService.verifyProvider(id, status));
	}
}