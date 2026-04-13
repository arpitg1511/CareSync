package com.app.caresync.client;

import com.app.caresync.dto.PatientResponse;
import com.app.caresync.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${PATIENT_SERVICE_URL:http://localhost:8083}", configuration = FeignConfig.class)
public interface PatientClient {

    @GetMapping("/api/patients/email/{email}")
    PatientResponse getPatientByEmail(@PathVariable("email") String email);
}
