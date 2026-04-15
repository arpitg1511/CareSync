package com.app.caresync.repository;

import com.app.caresync.model.Payment;
import com.app.caresync.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // PDF: findByAppointmentId()
    Optional<Payment> findByAppointmentId(Long appointmentId);

    // PDF: findByPatientId()
    List<Payment> findByPatientId(Long patientId);

    // PDF: getPaymentHistory — ordered
    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    // PDF: findByStatus()
    List<Payment> findByStatus(PaymentStatus status);

    // PDF: findByTransactionId()
    Optional<Payment> findByTransactionId(String transactionId);

    // PDF: findByProviderId()
    List<Payment> findByProviderId(Long providerId);

    // PDF: sumAmountByPatientId()
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patientId = :patientId AND p.status = 'PAID'")
    BigDecimal sumAmountByPatientId(Long patientId);

    // PDF: findByPaidAtBetween()
    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    // PDF: getTotalRevenue() for provider
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.providerId = :providerId AND p.status = 'PAID'")
    BigDecimal getTotalRevenueByProvider(Long providerId);
}
