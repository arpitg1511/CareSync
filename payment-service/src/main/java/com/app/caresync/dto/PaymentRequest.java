package com.app.caresync.dto;

import com.app.caresync.model.PaymentMode;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private BigDecimal amount;
    private PaymentMode mode;
    private String transactionId;
    private String currency;
    private String notes;
}
