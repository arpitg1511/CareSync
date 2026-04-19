package com.app.caresync.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProviderResponse {
    private Long providerId;
    private Long userId;
    private String fullName;
    private String email;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private Integer experienceMonths;
    private String bio;
    private String clinicName;
    private String clinicAddress;
    private String address;
    private String contact;
    private Double avgRating;
    private Boolean isVerified;
    private Boolean isAvailable;
    private String status;
    private LocalDateTime createdAt;
}
