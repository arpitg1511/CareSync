package com.app.caresync.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientRequest {
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
    private String medicalHistory;
}
