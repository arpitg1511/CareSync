package com.app.caresync.service;

import com.app.caresync.dto.ReviewRequest;
import com.app.caresync.dto.ReviewResponse;
import com.app.caresync.exception.ReviewNotFoundException;
import com.app.caresync.model.Review;
import com.app.caresync.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewResponse addReview(ReviewRequest request) {
        if (reviewRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new RuntimeException("Review already submitted for this appointment");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = Review.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewDate(LocalDate.now())
                .isVerified(true)
                .isAnonymous(request.getIsAnonymous() != null && request.getIsAnonymous())
                .isFlagged(false)
                .build();

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getByProvider(Long providerId) {
        return reviewRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getByPatient(Long patientId) {
        return reviewRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getByAppointment(Long appointmentId) {
        return reviewRepository.findByAppointmentId(appointmentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found for appointment " + appointmentId));
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getFlaggedReviews() {
        return reviewRepository.findByIsFlagged(true).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewResponse flagReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        review.setIsFlagged(true);
        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse unflagReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        review.setIsFlagged(false);
        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public Double getAvgRating(Long providerId) {
        Double avg = reviewRepository.avgRatingByProviderId(providerId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public long getReviewCount(Long providerId) {
        return reviewRepository.countByProviderId(providerId);
    }

    private ReviewResponse mapToResponse(Review r) {
        if (r == null) return null;
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .appointmentId(r.getAppointmentId())
                .patientId(r.getPatientId())
                .providerId(r.getProviderId())
                .rating(r.getRating())
                .comment(r.getComment())
                .reviewDate(r.getReviewDate())
                .isVerified(r.getIsVerified())
                .isAnonymous(r.getIsAnonymous())
                .isFlagged(r.getIsFlagged())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
