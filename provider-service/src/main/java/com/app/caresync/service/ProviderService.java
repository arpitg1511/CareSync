package com.app.caresync.service;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.model.ProviderStatus;

import java.util.List;

public interface ProviderService {
    ProviderResponse saveProvider(String email, ProviderRequest request);
    ProviderResponse getProviderById(Long providerId);
    ProviderResponse getProviderByEmail(String email);
    List<ProviderResponse> searchProviders(String query);
    List<ProviderResponse> getAllProviders();
    List<ProviderResponse> getProvidersBySpecialization(String specialization);
    ProviderResponse verifyProvider(Long providerId, ProviderStatus status);
    ProviderResponse setAvailability(Long providerId, Boolean isAvailable);
    ProviderResponse updateRating(Long providerId, Double newAvgRating);
    List<ProviderResponse> getPendingProviders();
    void deleteProvider(Long providerId);
    boolean existsById(Long id);
    void createProviderFromUser(java.util.Map<String, Object> data);
    ProviderResponse reapply(Long providerId);
}
