package com.app.caresync.service;

import com.app.caresync.client.AuthClient;
import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.dto.UserDTO;
import com.app.caresync.exception.ProviderNotFoundException;
import com.app.caresync.model.Provider;
import com.app.caresync.model.ProviderStatus;
import com.app.caresync.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AuthClient authClient;

    private ProviderResponse mapToResponse(Provider p) {
        if (p == null) return null;
        try {
            return ProviderResponse.builder()
                    .providerId(p.getProviderId())
                    .userId(p.getUserId())
                    .fullName(p.getFullName())
                    .email(p.getEmail())
                    .specialization(p.getSpecialization())
                    .qualification(p.getQualification())
                    .experienceYears(p.getExperienceYears() != null ? p.getExperienceYears() : 0)
                    .experienceMonths(p.getExperienceMonths() != null ? p.getExperienceMonths() : 0)
                    .bio(p.getBio())
                    .clinicName(p.getClinicName())
                    .clinicAddress(p.getClinicAddress() != null ? p.getClinicAddress() : p.getAddress())
                    .address(p.getAddress())
                    .contact(p.getContact())
                    .avgRating(p.getAvgRating() != null ? p.getAvgRating() : 0.0)
                    .isVerified(p.getIsVerified() != null ? p.getIsVerified() : (p.getStatus() == ProviderStatus.APPROVED))
                    .isAvailable(p.getIsAvailable() != null ? p.getIsAvailable() : true)
                    .status(p.getStatus() != null ? p.getStatus().name() : "PENDING")
                    .createdAt(p.getCreatedAt())
                    .build();
        } catch (Exception e) {
            // Log mapping error for a specific row
            System.err.println("Mapping error for provider ID " + p.getProviderId() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProviderResponse saveProvider(String email, ProviderRequest request) {
        UserDTO user = authClient.getUserByEmail(email);

        Provider provider = providerRepository.findByUserId(user.getUserId())
                .orElse(Provider.builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .status(ProviderStatus.PENDING)
                        .isAvailable(true)
                        .isVerified(false)
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
        if (request.getName() != null) provider.setFullName(request.getName());

        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse getProviderById(Long providerId) {
        return providerRepository.findById(providerId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));
    }

    @Override
    public ProviderResponse getProviderByEmail(String email) {
        return providerRepository.findByEmail(email)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with email: " + email));
    }

    @Override
    public List<ProviderResponse> searchProviders(String query) {
        return providerRepository.searchProviders(query).stream()
                .map(this::mapToResponse)
                .filter(res -> res != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAllApproved().stream()
                .map(this::mapToResponse)
                .filter(res -> res != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderResponse> getProvidersBySpecialization(String specialization) {
        return providerRepository.findBySpecializationApproved(specialization).stream()
                .map(this::mapToResponse)
                .filter(res -> res != null)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponse verifyProvider(Long providerId, ProviderStatus status) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));
        provider.setStatus(status);
        provider.setIsVerified(status == ProviderStatus.APPROVED);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse setAvailability(Long providerId, Boolean isAvailable) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));
        provider.setIsAvailable(isAvailable);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse updateRating(Long providerId, Double newAvgRating) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));
        provider.setAvgRating(newAvgRating);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public List<ProviderResponse> getPendingProviders() {
        return providerRepository.findByStatus(ProviderStatus.PENDING).stream()
                .map(this::mapToResponse)
                .filter(res -> res != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ProviderNotFoundException("Provider not found with id: " + providerId);
        }
        providerRepository.deleteById(providerId);
    }

    @Override
    public boolean existsById(Long id) {
        return providerRepository.existsById(id);
    }

    @Override
    public void createProviderFromUser(java.util.Map<String, Object> data) {
        System.out.println("📥 RECEIVED Sync Request for Doctor Profile: " + data.get("email"));
        Long userId = Long.valueOf(data.get("userId").toString());
        String name = (String) data.get("name");
        String email = (String) data.get("email");
        String speciality = (String) data.get("speciality");
        String contact = (String) data.get("contact");

        Provider provider = providerRepository.findByUserId(userId)
                .orElse(Provider.builder()
                        .userId(userId)
                        .status(ProviderStatus.PENDING)
                        .isVerified(false)
                        .isAvailable(true)
                        .avgRating(0.0)
                        .experienceYears(0)
                        .experienceMonths(0)
                        .build());
        
        provider.setFullName(name);
        provider.setEmail(email);
        provider.setContact(contact);
        provider.setSpecialization(speciality);
        
        providerRepository.save(provider);
    }
    @Override
    public ProviderResponse reapply(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));
        
        // Reset to PENDING and ensure isVerified is false
        provider.setStatus(ProviderStatus.PENDING);
        provider.setIsVerified(false);
        
        System.out.println("🔄 Entity Re-applying: " + provider.getEmail());
        return mapToResponse(providerRepository.save(provider));
    }
}
