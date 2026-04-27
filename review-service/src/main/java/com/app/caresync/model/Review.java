package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = "appointmentId"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false, unique = true)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long providerId;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 2000)
    private String comment;

    @Builder.Default
    private LocalDate reviewDate = LocalDate.now();

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isAnonymous = false;

    @Builder.Default
    private Boolean isFlagged = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
