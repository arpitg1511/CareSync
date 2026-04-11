package com.app.caresync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponse {

    private Long providerId;
    private String fullName;
    private String email;
    private String specialization;
    private Integer experienceMonths;
    private String clinicName;
    private String address;
    private String contact;
}
