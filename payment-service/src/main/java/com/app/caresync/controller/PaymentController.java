package com.app.caresync.controller;

import com.app.caresync.dto.PaymentRequest;
import com.app.caresync.dto.PaymentResponse;
import com.app.caresync.model.PaymentStatus;
import com.app.caresync.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // PDF: POST process payment
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> process(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    // PDF: GET by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.getPaymentByAppointment(appointmentId));
    }

    // PDF: GET payment status
    @GetMapping("/appointment/{appointmentId}/status")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of("status", paymentService.getPaymentStatus(appointmentId).name()));
    }

    // PDF: GET by patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatient(patientId));
    }

    // PDF: GET payment history (ordered)
    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(patientId));
    }

    // PDF: GET by provider earnings
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByProvider(providerId));
    }

    // PDF: GET total revenue
    @GetMapping("/provider/{providerId}/revenue")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<BigDecimal> getRevenue(@PathVariable Long providerId) {
        return ResponseEntity.ok(paymentService.getTotalRevenueByProvider(providerId));
    }

    // PDF: GET earnings summary (total/pending/refunded)
    @GetMapping("/provider/{providerId}/earnings")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getEarnings(@PathVariable Long providerId) {
        return ResponseEntity.ok(paymentService.getEarningsSummary(providerId));
    }

    // PDF: GET generateInvoice
    @GetMapping("/appointment/{appointmentId}/invoice")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getInvoice(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.generateInvoice(appointmentId));
    }

    // PDF: POST refund
    @PostMapping("/refund/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> refund(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.refundPayment(appointmentId));
    }

    // PDF: PUT updatePaymentStatus (doctor marks pay-at-clinic as paid)
    @PutMapping("/appointment/{appointmentId}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable Long appointmentId,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(appointmentId, status));
    }

    // PDF: Admin view all
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
