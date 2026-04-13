package com.app.caresync.service;

import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment bookAppointment(Appointment appointment);
    Optional<Appointment> getById(Long appointmentId);
    List<Appointment> getByPatient(Long patientId);
    List<Appointment> getByProvider(Long providerId);
    List<Appointment> getByProviderAndDate(Long providerId, LocalDate date);
    void cancelAppointment(Long appointmentId);
    void rescheduleAppointment(Long appointmentId, Long newSlotId);
    void completeAppointment(Long appointmentId);
    void updateStatus(Long appointmentId, AppointmentStatus status);
    List<Appointment> getUpcomingByPatient(Long patientId);
    long getAppointmentCount(Long providerId);
}
