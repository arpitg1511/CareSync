package com.app.caresync.service;

import com.app.caresync.dto.PatientRequest;
import com.app.caresync.dto.PatientResponse;

public interface PatientService {
    PatientResponse createProfile(String email, PatientRequest request);
    PatientResponse getById(Long patientId);
    PatientResponse getProfile(String email);
    PatientResponse updateProfile(String email, PatientRequest request);
    void createProfileFromUser(java.util.Map<String, Object> data);
}
