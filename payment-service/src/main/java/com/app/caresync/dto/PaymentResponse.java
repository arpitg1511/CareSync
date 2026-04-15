package com.app.caresync.dto;

import com.app.caresync.model.PaymentMode;
import com.app.caresync.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMode mode;
    private String transactionId;
    private String currency;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String notes;
    private LocalDateTime createdAt;
}
