package com.app.caresync.dto;

import lombok.Data;

@Data
public class ProviderRequest {

    private String specialization;
    private Integer experienceMonths;
    private String clinicName;
    private String address;
    private String contact;
}
