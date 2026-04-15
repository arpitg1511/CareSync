package com.app.caresync.service;

import com.app.caresync.dto.PaymentRequest;
import com.app.caresync.dto.PaymentResponse;
import com.app.caresync.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentByAppointment(Long appointmentId);
    List<PaymentResponse> getPaymentsByPatient(Long patientId);
    List<PaymentResponse> getPaymentHistory(Long patientId);
    PaymentStatus getPaymentStatus(Long appointmentId);
    Map<String, Object> generateInvoice(Long appointmentId);
    List<PaymentResponse> getPaymentsByProvider(Long providerId);
    List<PaymentResponse> getAllPayments();
    PaymentResponse refundPayment(Long appointmentId);
    PaymentResponse updatePaymentStatus(Long appointmentId, PaymentStatus newStatus);
    BigDecimal getTotalRevenueByProvider(Long providerId);
    Map<String, Object> getEarningsSummary(Long providerId);
}
