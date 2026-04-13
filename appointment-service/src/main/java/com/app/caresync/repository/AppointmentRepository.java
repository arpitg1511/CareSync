package com.app.caresync.repository;

import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByProviderId(Long providerId);
    List<Appointment> findBySlotId(Long slotId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByProviderIdAndAppointmentDate(Long providerId, LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patientId = ?1 AND a.appointmentDate >= CURRENT_DATE AND a.status = 'SCHEDULED'")
    List<Appointment> findUpcomingByPatientId(Long patientId);
    
    long countByProviderId(Long providerId);
}
