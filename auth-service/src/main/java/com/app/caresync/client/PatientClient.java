package com.app.caresync.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "patient-service", url = "${patient.service.url:http://localhost:8083}")
public interface PatientClient {
    
    @PostMapping("/api/patients/internal/create")
    void createPatientProfile(@RequestBody Map<String, Object> data);
}
