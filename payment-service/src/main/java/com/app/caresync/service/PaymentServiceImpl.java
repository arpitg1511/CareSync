package com.app.caresync.service;

import com.app.caresync.model.Payment;
import com.app.caresync.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Payment payment) {
        payment.setStatus("Paid");
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public List<Payment> getPaymentsByPatient(Long patientId) {
        return paymentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Payment> getPaymentHistory() {
        return paymentRepository.findAll();
    }

    @Override
    public void refundPayment(Long paymentId) {
        paymentRepository.findById(paymentId).ifPresent(p -> {
            p.setStatus("Refunded");
            p.setRefundedAt(LocalDateTime.now());
            paymentRepository.save(p);
        });
    }

    @Override
    public String getPaymentStatus(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(Payment::getStatus)
                .orElse("Unknown");
    }

    @Override
    public void updatePaymentStatus(Long paymentId, String status) {
        paymentRepository.findById(paymentId).ifPresent(p -> {
            p.setStatus(status);
            paymentRepository.save(p);
        });
    }

    @Override
    public String generateInvoice(Long paymentId) {
        // Mock invoice generation logic
        return "INV-" + paymentId;
    }

    @Override
    public Double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> "Paid".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}
