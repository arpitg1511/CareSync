package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    private Long userId;

    private String fullName;
    private String email;
    private String specialization;
    private String qualification;
    private Integer experienceMonths;
    private Integer experienceYears;
    
    @Column(columnDefinition = "TEXT")
    private String bio;

    private String clinicName;
    private String address;
    private String clinicAddress;
    private String contact;

    private Double avgRating = 0.0;

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isAvailable = true;

    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
}
