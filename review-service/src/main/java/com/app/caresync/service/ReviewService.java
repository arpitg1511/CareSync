package com.app.caresync.service;

import com.app.caresync.dto.ReviewRequest;
import com.app.caresync.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest request);
    List<ReviewResponse> getByProvider(Long providerId);
    List<ReviewResponse> getByPatient(Long patientId);
    ReviewResponse getByAppointment(Long appointmentId);
    List<ReviewResponse> getAllReviews();
    List<ReviewResponse> getFlaggedReviews();
    ReviewResponse updateReview(Long reviewId, ReviewRequest request);
    void deleteReview(Long reviewId);
    ReviewResponse flagReview(Long reviewId);
    ReviewResponse unflagReview(Long reviewId);
    Double getAvgRating(Long providerId);
    long getReviewCount(Long providerId);
}
