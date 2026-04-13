package com.app.caresync.controller;

import com.app.caresync.model.Payment;
import com.app.caresync.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentResource {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> process(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.processPayment(payment));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Payment> getByAppointment(@PathVariable Long appointmentId) {
        return paymentService.getPaymentByAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Payment>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatient(patientId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getHistory() {
        return ResponseEntity.ok(paymentService.getPaymentHistory());
    }

    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<?> refund(@PathVariable Long paymentId) {
        paymentService.refundPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalRevenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(paymentService.getTotalRevenue());
    }
}
