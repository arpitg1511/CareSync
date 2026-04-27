package com.app.caresync.service;

import com.app.caresync.model.Patient;
import com.app.caresync.repository.PatientRepository;
import com.app.caresync.client.AuthClient;
import com.app.caresync.dto.UserDTO;
import com.app.caresync.dto.PatientRequest;
import com.app.caresync.dto.PatientResponse;
import com.app.caresync.exception.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuthClient authClient;

    @Override
    public PatientResponse createProfile(String email, PatientRequest request) {
        UserDTO user = authClient.getUserByEmail(email);
        Patient patient = Patient.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .medicalHistory(request.getMedicalHistory())
                .build();
        return mapToResponse(patientRepository.save(patient));
    }

    @Override
    public PatientResponse getById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse getProfile(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient profile not found for email: " + email));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse updateProfile(String email, PatientRequest request) {
        Patient existing = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient profile not found for email: " + email));
        
        existing.setAddress(request.getAddress());
        existing.setEmergencyContact(request.getEmergencyContact());
        existing.setMedicalHistory(request.getMedicalHistory());
        existing.setBloodGroup(request.getBloodGroup());
        
        return mapToResponse(patientRepository.save(existing));
    }

    private PatientResponse mapToResponse(Patient p) {
        if (p == null) return null;
        return PatientResponse.builder()
                .patientId(p.getPatientId())
                .userId(p.getUserId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender())
                .bloodGroup(p.getBloodGroup())
                .address(p.getAddress())
                .emergencyContact(p.getEmergencyContact())
                .medicalHistory(p.getMedicalHistory())
                .build();
    }

    @Override
    public void createProfileFromUser(java.util.Map<String, Object> data) {
        System.out.println("RECEIVED Sync Request for Patient Profile: " + data.get("email"));
        Long userId = Long.valueOf(data.get("userId").toString());
        String name = (String) data.get("name");
        String email = (String) data.get("email");

        Patient patient = Patient.builder()
                .userId(userId)
                .fullName(name)
                .email(email)
                .build();
        
        patientRepository.save(patient);
    }
}
