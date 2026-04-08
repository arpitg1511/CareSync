package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder; // Added!
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Added!
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId; // Fixed naming

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String fullName; // Added for fast search! 🚀

    @Column(nullable = false)
    private String email;    // Added for decoupled lookups! 📧

    @Column(nullable = false)
    private String specialization; // Fixed typo

    private Integer experienceMonths;

    @Column(nullable = false)
    private String clinicName;

    private String address;

    @Column(nullable = false)
    private String contact;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProviderStatus status = ProviderStatus.PENDING;
}

