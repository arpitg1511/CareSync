package com.app.caresync.controller;

import com.app.caresync.model.Review;
import com.app.caresync.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewResource {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Review> add(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Review>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(reviewService.getByProvider(providerId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Review>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(reviewService.getByPatient(patientId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Review> getByAppointment(@PathVariable Long appointmentId) {
        return reviewService.getByAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable Long reviewId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avgRating")
    public ResponseEntity<Double> getAvgRating(@RequestParam Long providerId) {
        return ResponseEntity.ok(reviewService.getAvgRating(providerId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount(@RequestParam Long providerId) {
        return ResponseEntity.ok(reviewService.getReviewCount(providerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAll() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
