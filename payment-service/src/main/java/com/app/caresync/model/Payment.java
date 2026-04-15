package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false, unique = true)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    private Long providerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMode mode = PaymentMode.CASH;

    private String transactionId;

    @Builder.Default
    private String currency = "INR";

    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String notes;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
