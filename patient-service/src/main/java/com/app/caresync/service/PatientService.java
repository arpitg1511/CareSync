package com.app.caresync.service;

import com.app.caresync.model.Patient;
import com.app.caresync.repository.PatientRepository;
import com.app.caresync.client.AuthClient;
import com.app.caresync.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuthClient authClient;

    public Patient createProfile(String email, Patient request) {
        // 📞 Call the Cloud Auth Service to get UserID and Name
        UserDTO user = authClient.getUserByEmail(email);

        request.setUserId(user.getUserId());
        request.setFullName(user.getFullName());
        request.setEmail(user.getEmail());

        return patientRepository.save(request);
    }

    public Patient getProfile(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient profile not found!"));
    }

    public Patient updateProfile(String email, Patient updatedData) {
        Patient existing = getProfile(email);
        
        existing.setAddress(updatedData.getAddress());
        existing.setEmergencyContact(updatedData.getEmergencyContact());
        existing.setMedicalHistory(updatedData.getMedicalHistory());
        existing.setBloodGroup(updatedData.getBloodGroup());
        
        return patientRepository.save(existing);
    }
}
