package com.app.caresync.repository;

import com.app.caresync.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProviderId(Long providerId);
    List<Review> findByPatientId(Long patientId);
    Optional<Review> findByAppointmentId(Long appointmentId);
    List<Review> findByRating(Integer rating);
    boolean existsByAppointmentId(Long appointmentId);
    long countByProviderId(Long providerId);
    List<Review> findByIsFlagged(Boolean isFlagged);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.providerId = :providerId AND r.isFlagged = false")
    Double avgRatingByProviderId(Long providerId);
}
