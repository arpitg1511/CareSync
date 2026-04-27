package com.app.caresync.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Integer rating;
    private String comment;
    private Boolean isAnonymous;
}
