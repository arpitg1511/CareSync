package com.app.caresync.service;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.model.Provider;
import com.app.caresync.model.ProviderStatus;
import com.app.caresync.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private com.app.caresync.client.AuthClient authClient;

    private ProviderResponse mapToResponse(Provider p) {
        return ProviderResponse.builder()
                .providerId(p.getProviderId())
                .userId(p.getUserId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .specialization(p.getSpecialization())
                .qualification(p.getQualification())
                .experienceYears(p.getExperienceYears())
                .experienceMonths(p.getExperienceMonths())
                .bio(p.getBio())
                .clinicName(p.getClinicName())
                .clinicAddress(p.getClinicAddress() != null ? p.getClinicAddress() : p.getAddress())
                .address(p.getAddress())
                .contact(p.getContact())
                .avgRating(p.getAvgRating())
                .isVerified(p.getIsVerified())
                .isAvailable(p.getIsAvailable())
                .status(p.getStatus() != null ? p.getStatus().name() : "PENDING")
                .createdAt(p.getCreatedAt())
                .build();
    }

    public ProviderResponse saveProvider(String email, ProviderRequest request) {
        com.app.caresync.dto.UserDTO user = authClient.getUserByEmail(email);

        // Update if exists, else create
        Provider provider = providerRepository.findByUserId(user.getUserId())
                .orElse(Provider.builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .status(ProviderStatus.PENDING)
                        .build());

        if (request.getSpecialization() != null) provider.setSpecialization(request.getSpecialization());
        if (request.getQualification() != null) provider.setQualification(request.getQualification());
        if (request.getExperienceYears() != null) provider.setExperienceYears(request.getExperienceYears());
        if (request.getExperienceMonths() != null) provider.setExperienceMonths(request.getExperienceMonths());
        if (request.getBio() != null) provider.setBio(request.getBio());
        if (request.getClinicName() != null) provider.setClinicName(request.getClinicName());
        if (request.getClinicAddress() != null) provider.setClinicAddress(request.getClinicAddress());
        if (request.getAddress() != null) provider.setAddress(request.getAddress());
        if (request.getContact() != null) provider.setContact(request.getContact());

        return mapToResponse(providerRepository.save(provider));
    }

    public ProviderResponse getProviderById(Long providerId) {
        return providerRepository.findById(providerId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
    }

    public List<ProviderResponse> searchProviders(String query) {
        return providerRepository.searchProviders(query).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAllProvidersByStatus(ProviderStatus.APPROVED).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProviderResponse> getProvidersBySpecialization(String specialization) {
        return providerRepository.findBySpecialization(specialization).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public ProviderResponse verifyProvider(Long providerId, ProviderStatus status) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        provider.setStatus(status);
        provider.setIsVerified(status == ProviderStatus.APPROVED);
        return mapToResponse(providerRepository.save(provider));
    }

    public ProviderResponse setAvailability(Long providerId, Boolean isAvailable) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        provider.setIsAvailable(isAvailable);
        return mapToResponse(providerRepository.save(provider));
    }

    // Called by review-service after new rating
    public ProviderResponse updateRating(Long providerId, Double newAvgRating) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        provider.setAvgRating(newAvgRating);
        return mapToResponse(providerRepository.save(provider));
    }

    public List<ProviderResponse> getPendingProviders() {
        return providerRepository.findAllProvidersByStatus(ProviderStatus.PENDING).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public void deleteProvider(Long providerId) {
        providerRepository.deleteById(providerId);
    }

    public boolean existsById(Long id) {
        return providerRepository.existsById(id);
    }
}
