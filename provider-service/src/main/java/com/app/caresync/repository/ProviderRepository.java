package com.app.caresync.repository;

import com.app.caresync.dto.ProviderResponse;
import com.app.caresync.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import com.app.caresync.model.ProviderStatus;


@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    // 🔍 Search for Doctors!
    // This finds providers where specialization or clinicName matches the query
    // 🚀 THE ULTIMATE SEARCH: Name, Specialization, Clinic, or Address! 🦾
    @Query("SELECT p FROM Provider p WHERE " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.clinicName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND p.status = com.app.caresync.model.ProviderStatus.APPROVED")
    List<Provider> searchProviders(@Param("query") String query);
    
    List<Provider> findAllProvidersByStatus(ProviderStatus status);

    List<Provider> findBySpecialization(String spec);
}
