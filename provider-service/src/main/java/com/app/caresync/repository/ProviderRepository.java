package com.app.caresync.repository;

import com.app.caresync.model.Provider;
import com.app.caresync.model.ProviderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUserId(Long userId);
    Optional<Provider> findByEmail(String email);

    // PDF: findBySpecialization()
    List<Provider> findBySpecializationIgnoreCase(String specialization);

    // PDF: findByIsVerified()
    List<Provider> findByIsVerified(Boolean isVerified);

    // PDF: findByIsAvailable()
    List<Provider> findByIsAvailable(Boolean isAvailable);

    // PDF: findByClinicAddress()
    List<Provider> findByClinicAddressContainingIgnoreCase(String location);

    // PDF: countBySpecialization()
    long countBySpecialization(String specialization);

    List<Provider> findByStatus(ProviderStatus status);

    // PDF: searchByNameOrSpecialization() - Full text search
    @Query("SELECT p FROM Provider p WHERE " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.clinicName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.clinicAddress) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND p.status = 'APPROVED'")
    List<Provider> searchProviders(@Param("query") String query);
}
