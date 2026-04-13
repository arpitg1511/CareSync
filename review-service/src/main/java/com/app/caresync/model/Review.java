package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(unique = true)
    private Long appointmentId;

    private Long patientId;

    private Long providerId;

    private Integer rating; // 1-5

    private String comment;

    private LocalDateTime reviewDate = LocalDateTime.now();

    private Boolean isVerified = false;

    private Boolean isAnonymous = false;
}
