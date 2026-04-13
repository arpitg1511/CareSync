package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private Long patientId;

    private Long providerId;

    private Long slotId;

    private String serviceType;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; // Scheduled, Completed, Cancelled, No-Show

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String modeOfConsultation; // In-Person, Teleconsultation

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Mapping for current code compatibility
    private String reason;
    private String doctorNotes;
    private LocalDateTime appointmentDateTime;
}
