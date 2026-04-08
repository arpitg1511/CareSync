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

    // Helper: Map Entity to DTO
    private ProviderResponse mapToResponse(Provider p) {
        // 🚀 Microservice optimization: Use local name/email instead of calling Auth service!
        return ProviderResponse.builder()
                .providerId(p.getProviderId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .specialization(p.getSpecialization())
                .experienceMonths(p.getExperienceMonths())
                .clinicName(p.getClinicName())
                .address(p.getAddress())
                .contact(p.getContact())
                .build();
    }

    // 🩺 1. SAVE or UPDATE Profile
    public ProviderResponse saveProvider(String email, ProviderRequest request) {
        com.app.caresync.dto.UserDTO user = authClient.getUserByEmail(email);

        Provider provider = Provider.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName()) // 🏁 Saved locally!
                .email(user.getEmail())       // 🏁 Saved locally!
                .specialization(request.getSpecialization())
                .experienceMonths(request.getExperienceMonths())
                .clinicName(request.getClinicName())
                .address(request.getAddress())
                .contact(request.getContact())
                .status(ProviderStatus.PENDING)
                .build();

        Provider savedProvider = providerRepository.save(provider);
        return mapToResponse(savedProvider);
    }

    public List<ProviderResponse> searchProviders(String query) {
        return providerRepository.searchProviders(query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAllProvidersByStatus(ProviderStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public ProviderResponse verifyProvider(Long providerId, ProviderStatus status) {
    	
    	Provider provider = providerRepository.findById(providerId)
    			.orElseThrow(() -> new RuntimeException("User not found!"));
    	
    	provider.setStatus(status);
    	Provider updatedProvider = providerRepository.save(provider);
    	
    	return mapToResponse(updatedProvider);
    }
    
    
    public List<ProviderResponse> getPendingProviders() {
    	return providerRepository.findAllProvidersByStatus(ProviderStatus.PENDING)
    			.stream()
    			.map(this::mapToResponse)
    			.collect(Collectors.toList());
    }
}
