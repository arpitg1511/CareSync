package com.app.caresync.dto;

import com.app.caresync.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Long slotId;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private String doctorNotes;
    private String modeOfConsultation;
    private String serviceType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
