package com.app.caresync.repository;

import com.app.caresync.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointmentId(Long appointmentId);
    List<Payment> findByPatientId(Long patientId);
    List<Payment> findByStatus(String status);
    Optional<Payment> findByTransactionId(String transactionId);
}
