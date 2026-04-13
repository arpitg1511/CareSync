package com.app.caresync.service;

import com.app.caresync.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(Payment payment);
    Optional<Payment> getPaymentByAppointment(Long appointmentId);
    List<Payment> getPaymentsByPatient(Long patientId);
    List<Payment> getPaymentHistory();
    void refundPayment(Long paymentId);
    String getPaymentStatus(Long paymentId);
    void updatePaymentStatus(Long paymentId, String status);
    String generateInvoice(Long paymentId);
    Double getTotalRevenue();
}
