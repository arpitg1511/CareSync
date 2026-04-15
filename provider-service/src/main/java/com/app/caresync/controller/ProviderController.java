package com.app.caresync.controller;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.model.ProviderStatus;
import com.app.caresync.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // PDF: GET all verified providers (guest/patient)
    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAll() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    // PDF: GET provider by id
    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    // PDF: GET by specialization
    @GetMapping("/specialization/{spec}")
    public ResponseEntity<List<ProviderResponse>> getBySpec(@PathVariable String spec) {
        return ResponseEntity.ok(providerService.getProvidersBySpecialization(spec));
    }

    // PDF: full-text search
    @GetMapping("/search")
    public ResponseEntity<List<ProviderResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(providerService.searchProviders(query));
    }

    // PDF: POST register/update provider profile
    @PostMapping("/profile")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ProviderResponse> saveProfile(Authentication auth, @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.saveProvider(auth.getName(), request));
    }

    // Internal: check provider exists (appointment-service)
    @GetMapping("/internal/{id}/exists")
    public boolean checkExists(@PathVariable Long id) {
        return providerService.existsById(id);
    }

    // PDF: Admin approve
    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProviderResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.verifyProvider(id, ProviderStatus.APPROVED));
    }

    // PDF: Admin reject
    @PutMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProviderResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.verifyProvider(id, ProviderStatus.REJECTED));
    }

    // PDF: Admin list pending
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProviderResponse>> getPending() {
        return ResponseEntity.ok(providerService.getPendingProviders());
    }

    // PDF: Admin delete provider
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    // PDF: setAvailability
    @PutMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ProviderResponse> setAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        return ResponseEntity.ok(providerService.setAvailability(id, available));
    }

    // Internal: update avgRating (called by review-service)
    @PutMapping("/internal/{id}/rating")
    public ResponseEntity<ProviderResponse> updateRating(@PathVariable Long id, @RequestParam Double avgRating) {
        return ResponseEntity.ok(providerService.updateRating(id, avgRating));
    }
}
