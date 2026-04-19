package com.app.caresync.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecordRequest {
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
}
