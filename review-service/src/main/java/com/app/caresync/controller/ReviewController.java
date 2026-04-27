package com.app.caresync.controller;

import com.app.caresync.dto.ReviewRequest;
import com.app.caresync.dto.ReviewResponse;
import com.app.caresync.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ⭐ Submit review (patients only, after completed appointment)
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    // 📋 Get reviews for a provider (public)
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ReviewResponse>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(reviewService.getByProvider(providerId));
    }

    // 👤 Patient's reviews
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(reviewService.getByPatient(patientId));
    }

    // 📑 Get review by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ReviewResponse> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(reviewService.getByAppointment(appointmentId));
    }

    // 🌟 Get average rating for a provider (public)
    @GetMapping("/provider/{providerId}/avg")
    public ResponseEntity<Map<String, Object>> getAvgRating(@PathVariable Long providerId) {
        return ResponseEntity.ok(Map.of(
                "avgRating", reviewService.getAvgRating(providerId),
                "totalReviews", reviewService.getReviewCount(providerId)
        ));
    }

    // ✏️ Update review
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    // 🚩 Flag review (provider requests moderation)
    @PutMapping("/{reviewId}/flag")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ReviewResponse> flag(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.flagReview(reviewId));
    }

    // 👑 Admin: all reviews
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // 👑 Admin: flagged reviews for moderation
    @GetMapping("/admin/flagged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getFlagged() {
        return ResponseEntity.ok(reviewService.getFlaggedReviews());
    }

    // 👑 Admin: unflag review
    @PutMapping("/admin/{reviewId}/unflag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> unflag(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.unflagReview(reviewId));
    }

    // 👑 Admin: delete review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
