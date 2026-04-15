package com.app.caresync.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long providerId;
    private Long slotId;                  // optional – links to schedule-service slot
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String modeOfConsultation;    // IN_PERSON | TELECONSULTATION
    private String serviceType;           // General Consultation, Follow-Up, etc.
}
