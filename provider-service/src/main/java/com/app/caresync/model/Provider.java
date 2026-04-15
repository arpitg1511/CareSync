package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Provider {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String specialization;

    private String qualification;       // PDF: qualification field
    private Integer experienceYears;    // PDF: experienceYears (renamed from months)
    private Integer experienceMonths;   // kept for legacy compat

    @Column(length = 2000)
    private String bio;                 // PDF: bio field

    private String clinicName;
    private String clinicAddress;       // PDF: clinicAddress
    private String address;             // alias

    private String contact;

    @Builder.Default
    private Double avgRating = 0.0;    // PDF: avgRating field

    @Builder.Default
    private Boolean isVerified = false; // PDF: isVerified field

    @Builder.Default
    private Boolean isAvailable = true; // PDF: isAvailable field

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProviderStatus status = ProviderStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // PDF: createdAt field
}
