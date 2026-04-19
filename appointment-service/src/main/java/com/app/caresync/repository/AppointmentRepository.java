package com.app.caresync.repository;

import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // PDF: findByPatientId()
    List<Appointment> findByPatientId(Long patientId);

    // PDF: findByProviderId()
    List<Appointment> findByProviderId(Long providerId);

    // PDF: findBySlotId()
    List<Appointment> findBySlotId(Long slotId);

    // PDF: findByStatus()
    List<Appointment> findByStatus(AppointmentStatus status);

    // PDF: findByProviderIdAndAppointmentDate()
    List<Appointment> findByProviderIdAndAppointmentDateTimeBetween(
            Long providerId, LocalDateTime start, LocalDateTime end);

    // PDF: findUpcomingByPatientId()
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND a.appointmentDateTime >= CURRENT_TIMESTAMP " +
           "AND a.status IN ('SCHEDULED', 'RESCHEDULED') ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingByPatientId(Long patientId);

    // PDF: countByProviderId()
    long countByProviderId(Long providerId);
    long countByProviderIdAndStatus(Long providerId, AppointmentStatus status);

    // For NoShowDetectionScheduler
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' " +
           "AND a.appointmentDateTime < :cutoff")
    List<Appointment> findScheduledAppointmentsBeforeDateTime(LocalDateTime cutoff);
}
