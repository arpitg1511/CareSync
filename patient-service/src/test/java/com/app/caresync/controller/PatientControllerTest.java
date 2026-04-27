package com.app.caresync.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.app.caresync.dto.PatientResponse;
import com.app.caresync.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple testing
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;



    @Test
    @WithMockUser(username = "john@example.com")
    void getMyProfile_ShouldReturnProfile() throws Exception {
        PatientResponse response = PatientResponse.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        when(patientService.getProfile(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/patients/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getById_ShouldReturnPatient() throws Exception {
        PatientResponse response = PatientResponse.builder()
                .patientId(1L)
                .fullName("Jane Doe")
                .build();

        when(patientService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }
}
