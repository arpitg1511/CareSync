package com.app.caresync.controller;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.service.ProviderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> saveProfile(Authentication authentication, @RequestBody ProviderRequest request) {
        ProviderResponse response = providerService.saveProvider(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProfile(@RequestParam String query) {
        List<ProviderResponse> providers = providerService.searchProviders(query);

        return ResponseEntity.ok(providers);
    }
}
