package com.app.caresync.service;

import com.app.caresync.dto.ProviderRequest;
import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.dto.UserDTO;
import com.app.caresync.model.Provider;
import com.app.caresync.model.ProviderStatus;
import com.app.caresync.repository.ProviderRepository;
import com.app.caresync.client.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AuthClient authClient;

    // 🔄 Mapping Helper
    private ProviderResponse mapToResponse(Provider p) {
        if (p == null) return null;
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

    @Override
    public ProviderResponse saveProvider(String email, ProviderRequest request) {
        UserDTO user = authClient.getUserByEmail(email);
        Provider provider = Provider.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .specialization(request.getSpecialization())
                .experienceMonths(request.getExperienceMonths())
                .clinicName(request.getClinicName())
                .address(request.getAddress())
                .contact(request.getContact())
                .status(ProviderStatus.PENDING)
                .isAvailable(true)
                .isVerified(false)
                .build();
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    @Cacheable(value = "providers", key = "#query")
    public List<ProviderResponse> searchProviders(String query) {
        return providerRepository.searchProviders(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAllProvidersByStatus(ProviderStatus.APPROVED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponse verifyProvider(Long providerId, ProviderStatus status) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        provider.setStatus(status);
        provider.setIsVerified(status == ProviderStatus.APPROVED);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public List<ProviderResponse> getPendingProviders() {
        return providerRepository.findAllProvidersByStatus(ProviderStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return providerRepository.existsById(id);
    }

    @Override
    public List<ProviderResponse> getBySpecialization(String spec) {
        return providerRepository.findBySpecialization(spec).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAvailability(Long id, Boolean status) {
        providerRepository.findById(id).ifPresent(p -> {
            p.setIsAvailable(status);
            providerRepository.save(p);
        });
    }
}
