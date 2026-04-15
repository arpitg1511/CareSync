package com.app.caresync.dto;

import lombok.Data;

@Data
public class ProviderRequest {
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private Integer experienceMonths;  // legacy
    private String bio;
    private String clinicName;
    private String clinicAddress;
    private String address;
    private String contact;
}
