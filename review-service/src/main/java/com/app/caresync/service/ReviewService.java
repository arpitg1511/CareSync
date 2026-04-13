package com.app.caresync.service;

import com.app.caresync.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(Review review);
    List<Review> getByProvider(Long providerId);
    List<Review> getByPatient(Long patientId);
    Optional<Review> getByAppointment(Long appointmentId);
    Review updateReview(Long reviewId, Review review);
    void deleteReview(Long reviewId);
    Double getAvgRating(Long providerId);
    long getReviewCount(Long providerId);
    List<Review> getAllReviews();
}
