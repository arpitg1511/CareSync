package com.app.caresync.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.caresync.client.AuthClient;
import com.app.caresync.dto.PatientRequest;
import com.app.caresync.dto.PatientResponse;
import com.app.caresync.dto.UserDTO;
import com.app.caresync.exception.PatientNotFoundException;
import com.app.caresync.model.Patient;
import com.app.caresync.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient samplePatient;
    private PatientRequest sampleRequest;
    private UserDTO sampleUser;

    @BeforeEach
    void setUp() {
        samplePatient = Patient.builder()
                .patientId(1L)
                .userId(100L)
                .fullName("John Doe")
                .email("john@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .bloodGroup("O+")
                .address("123 Main St")
                .emergencyContact("9876543210")
                .medicalHistory("None")
                .build();

        sampleRequest = new PatientRequest();
        sampleRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        sampleRequest.setGender("Male");
        sampleRequest.setBloodGroup("O+");
        sampleRequest.setAddress("123 Main St");
        sampleRequest.setEmergencyContact("9876543210");
        sampleRequest.setMedicalHistory("None");

        sampleUser = new UserDTO();
        sampleUser.setUserId(100L);
        sampleUser.setFullName("John Doe");
        sampleUser.setEmail("john@example.com");
    }

    @Test
    void createProfile_ShouldReturnPatientResponse() {
        when(authClient.getUserByEmail("john@example.com")).thenReturn(sampleUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        PatientResponse response = patientService.createProfile("john@example.com", sampleRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        verify(authClient).getUserByEmail("john@example.com");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void getById_WhenPatientExists_ShouldReturnResponse() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));

        PatientResponse response = patientService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPatientId());
        verify(patientRepository).findById(1L);
    }

    @Test
    void getById_WhenPatientDoesNotExist_ShouldThrowException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getById(1L));
        verify(patientRepository).findById(1L);
    }

    @Test
    void getProfile_WhenEmailExists_ShouldReturnResponse() {
        when(patientRepository.findByEmail("john@example.com")).thenReturn(Optional.of(samplePatient));

        PatientResponse response = patientService.getProfile("john@example.com");

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        verify(patientRepository).findByEmail("john@example.com");
    }

    @Test
    void updateProfile_ShouldModifyAndSavePatient() {
        when(patientRepository.findByEmail("john@example.com")).thenReturn(Optional.of(samplePatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        PatientResponse response = patientService.updateProfile("john@example.com", sampleRequest);

        assertNotNull(response);
        verify(patientRepository).findByEmail("john@example.com");
        verify(patientRepository).save(any(Patient.class));
    }
}
