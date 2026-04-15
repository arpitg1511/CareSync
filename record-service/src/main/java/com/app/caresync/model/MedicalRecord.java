package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records", uniqueConstraints = @UniqueConstraint(columnNames = "appointmentId"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false, unique = true)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long providerId;

    @Column(nullable = false, length = 5000)
    private String diagnosis;

    @Column(length = 5000)
    private String prescription;

    @Column(length = 8000)
    private String notes;

    private String attachmentUrl;

    private LocalDate followUpDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
