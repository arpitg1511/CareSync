package com.app.caresync.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class ReviewResponse {
    private Long reviewId;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Integer rating;
    private String comment;
    private LocalDate reviewDate;
    private Boolean isVerified;
    private Boolean isAnonymous;
    private Boolean isFlagged;
    private LocalDateTime createdAt;
}
