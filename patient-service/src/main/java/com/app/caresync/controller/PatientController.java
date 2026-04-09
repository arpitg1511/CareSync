package com.app.caresync.controller;

import com.app.caresync.dto.PatientRequest;
import com.app.caresync.dto.PatientResponse;
import com.app.caresync.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Register / Update Patient Profile
    @PostMapping("/profile")
    public ResponseEntity<PatientResponse> createProfile(@RequestBody PatientRequest profile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(patientService.createProfile(email, profile));
    }

    // Get My Profile
    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(patientService.getProfile(email));
    }

    // Update My Profile
    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateProfile(@RequestBody PatientRequest profile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(patientService.updateProfile(email, profile));
    }
}
