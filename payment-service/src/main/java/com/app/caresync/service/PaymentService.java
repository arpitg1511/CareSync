package com.app.caresync.service;

import com.app.caresync.dto.PaymentRequest;
import com.app.caresync.dto.PaymentResponse;
import com.app.caresync.model.Payment;
import com.app.caresync.model.PaymentMode;
import com.app.caresync.model.PaymentStatus;
import com.app.caresync.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // PDF: processPayment()
    public PaymentResponse processPayment(PaymentRequest request) {
        paymentRepository.findByAppointmentId(request.getAppointmentId()).ifPresent(p -> {
            throw new RuntimeException("Payment already exists for appointment " + request.getAppointmentId());
        });

        PaymentMode mode = request.getMode() != null ? request.getMode() : PaymentMode.CASH;
        boolean isOnline = mode != PaymentMode.CASH;

        Payment payment = Payment.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .amount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO)
                .mode(mode)
                .transactionId(request.getTransactionId() != null ? request.getTransactionId()
                        : (isOnline ? "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : null))
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .notes(request.getNotes())
                .status(isOnline ? PaymentStatus.PAID : PaymentStatus.PENDING)
                .paidAt(isOnline ? LocalDateTime.now() : null)
                .build();

        return mapToResponse(paymentRepository.save(payment));
    }

    // PDF: getPaymentByAppointment()
    public PaymentResponse getPaymentByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Payment not found for appointment " + appointmentId));
    }

    // PDF: getPaymentsByPatient()
    public List<PaymentResponse> getPaymentsByPatient(Long patientId) {
        return paymentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getPaymentHistory() — alias for patient, ordered by date
    public List<PaymentResponse> getPaymentHistory(Long patientId) {
        return paymentRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getPaymentStatus()
    public PaymentStatus getPaymentStatus(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(Payment::getStatus)
                .orElseThrow(() -> new RuntimeException("Payment not found for appointment " + appointmentId));
    }

    // PDF: generateInvoice() — returns structured invoice data
    public Map<String, Object> generateInvoice(Long appointmentId) {
        Payment p = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for appointment " + appointmentId));

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("invoiceNumber", "INV-" + p.getPaymentId() + "-" + appointmentId);
        invoice.put("appointmentId", p.getAppointmentId());
        invoice.put("patientId", p.getPatientId());
        invoice.put("providerId", p.getProviderId());
        invoice.put("amount", p.getAmount());
        invoice.put("currency", p.getCurrency());
        invoice.put("paymentMode", p.getMode());
        invoice.put("transactionId", p.getTransactionId());
        invoice.put("status", p.getStatus());
        invoice.put("paidAt", p.getPaidAt());
        invoice.put("generatedAt", LocalDateTime.now());
        return invoice;
    }

    public List<PaymentResponse> getPaymentsByProvider(Long providerId) {
        return paymentRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: refundPayment()
    public PaymentResponse refundPayment(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Only PAID payments can be refunded");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        return mapToResponse(paymentRepository.save(payment));
    }

    // PDF: updatePaymentStatus()
    public PaymentResponse updatePaymentStatus(Long appointmentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(newStatus);
        if (newStatus == PaymentStatus.PAID) payment.setPaidAt(LocalDateTime.now());
        return mapToResponse(paymentRepository.save(payment));
    }

    // PDF: getTotalRevenue() per provider
    public BigDecimal getTotalRevenueByProvider(Long providerId) {
        BigDecimal total = paymentRepository.getTotalRevenueByProvider(providerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Provider earnings summary: total, pending, refunded
    public Map<String, Object> getEarningsSummary(Long providerId) {
        List<Payment> payments = paymentRepository.findByProviderId(providerId);
        BigDecimal total = payments.stream().filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pending = payments.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refunded = payments.stream().filter(p -> p.getStatus() == PaymentStatus.REFUNDED)
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalCollected", total, "pending", pending, "refunded", refunded,
                "currency", "INR");
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId()).appointmentId(p.getAppointmentId())
                .patientId(p.getPatientId()).providerId(p.getProviderId())
                .amount(p.getAmount()).status(p.getStatus()).mode(p.getMode())
                .transactionId(p.getTransactionId()).currency(p.getCurrency())
                .paidAt(p.getPaidAt()).refundedAt(p.getRefundedAt())
                .notes(p.getNotes()).createdAt(p.getCreatedAt()).build();
    }
}
