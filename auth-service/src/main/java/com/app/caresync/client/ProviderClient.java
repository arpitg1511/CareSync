package com.app.caresync.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "provider-service", url = "${provider.service.url:http://localhost:8082}")
public interface ProviderClient {
    
    @PostMapping("/api/providers/internal/create")
    void createProviderProfile(@RequestBody Map<String, Object> providerData);
}
