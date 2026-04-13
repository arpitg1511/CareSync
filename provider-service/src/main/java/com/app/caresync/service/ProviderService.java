package com.app.caresync.service;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.model.ProviderStatus;
import java.util.List;

public interface ProviderService {
    ProviderResponse saveProvider(String email, ProviderRequest request);
    List<ProviderResponse> searchProviders(String query);
    List<ProviderResponse> getAllProviders();
    ProviderResponse verifyProvider(Long providerId, ProviderStatus status);
    List<ProviderResponse> getPendingProviders();
    boolean existsById(Long id);
    List<ProviderResponse> getBySpecialization(String spec);
    void updateAvailability(Long id, Boolean status);
}
