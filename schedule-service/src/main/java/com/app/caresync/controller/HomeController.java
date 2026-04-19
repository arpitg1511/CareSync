package com.app.caresync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "service", "CareSync Schedule Service",
            "status", "UP",
            "message", "Welcome to CareSync Schedule Service. Use /api/slots for endpoints."
        );
    }
}
