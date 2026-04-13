package com.app.caresync.repository;

import com.app.caresync.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProviderId(Long providerId);
    List<Review> findByPatientId(Long patientId);
    Optional<Review> findByAppointmentId(Long appointmentId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.providerId = ?1")
    Double avgRatingByProviderId(Long providerId);
    
    long countByProviderId(Long providerId);
    boolean existsByAppointmentId(Long appointmentId);
}
