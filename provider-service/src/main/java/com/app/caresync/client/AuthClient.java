package com.app.caresync.client;

import com.app.caresync.dto.UserDTO;
import com.app.caresync.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "https://auth-service-lm7j.onrender.com", configuration = FeignConfig.class)
public interface AuthClient {

    @GetMapping("/internal/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}
