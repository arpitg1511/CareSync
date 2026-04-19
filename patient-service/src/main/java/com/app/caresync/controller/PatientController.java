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

    // Internal: Get patient by their patientId (called by frontend for doctor's appointment view)
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    // 🔐 Internal: Get patient by email (called by appointment-service)
    @GetMapping("/email/{email}")
    public ResponseEntity<PatientResponse> getUserByEmailInternal(@PathVariable String email) {
        return ResponseEntity.ok(patientService.getProfile(email));
    }

    // 🔐 Internal: Sync profile from auth-service (called on registration)
    @PostMapping("/internal/create")
    public void createProfileInternal(@RequestBody java.util.Map<String, Object> data) {
        patientService.createProfileFromUser(data);
    }
}
