package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long appointmentId;

    private Long patientId;

    private Double amount;

    private String status; // Pending, Paid, Refunded, Failed

    private String mode; // Card, UPI, Wallet, Cash

    private String transactionId;

    private String currency = "INR";

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    private String notes;
}
