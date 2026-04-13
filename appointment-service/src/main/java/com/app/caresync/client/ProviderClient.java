package com.app.caresync.client;

import com.app.caresync.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "provider-service", url = "${PROVIDER_SERVICE_URL:http://localhost:8082}", configuration = FeignConfig.class)
public interface ProviderClient {

    @GetMapping("/api/providers/internal/{id}/exists")
    boolean checkIfProviderExists(@PathVariable("id") Long id);
}
