package com.app.caresync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "service", "CareSync Medical Record Service",
            "status", "UP",
            "message", "Welcome to the Medical Record Service."
        );
    }
}
