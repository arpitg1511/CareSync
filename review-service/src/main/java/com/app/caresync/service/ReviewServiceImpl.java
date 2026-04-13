package com.app.caresync.service;

import com.app.caresync.model.Review;
import com.app.caresync.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review review) {
        if (reviewRepository.existsByAppointmentId(review.getAppointmentId())) {
            throw new RuntimeException("Review already exists for this appointment");
        }
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getByProvider(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    @Override
    public List<Review> getByPatient(Long patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    @Override
    public Optional<Review> getByAppointment(Long appointmentId) {
        return reviewRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public Review updateReview(Long reviewId, Review review) {
        review.setReviewId(reviewId);
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Double getAvgRating(Long providerId) {
        Double avg = reviewRepository.avgRatingByProviderId(providerId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getReviewCount(Long providerId) {
        return reviewRepository.countByProviderId(providerId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
