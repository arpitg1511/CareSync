package com.app.caresync.service;

import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import com.app.caresync.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Appointment bookAppointment(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> getById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    @Override
    public List<Appointment> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getByProvider(Long providerId) {
        return appointmentRepository.findByProviderId(providerId);
    }

    @Override
    public List<Appointment> getByProviderAndDate(Long providerId, LocalDate date) {
        return appointmentRepository.findByProviderIdAndAppointmentDate(providerId, date);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(a -> {
            a.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(a);
        });
    }

    @Override
    public void rescheduleAppointment(Long appointmentId, Long newSlotId) {
        appointmentRepository.findById(appointmentId).ifPresent(a -> {
            a.setSlotId(newSlotId);
            appointmentRepository.save(a);
        });
    }

    @Override
    public void completeAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(a -> {
            a.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(a);
        });
    }

    @Override
    public void updateStatus(Long appointmentId, AppointmentStatus status) {
        appointmentRepository.findById(appointmentId).ifPresent(a -> {
            a.setStatus(status);
            appointmentRepository.save(a);
        });
    }

    @Override
    public List<Appointment> getUpcomingByPatient(Long patientId) {
        return appointmentRepository.findUpcomingByPatientId(patientId);
    }

    @Override
    public long getAppointmentCount(Long providerId) {
        return appointmentRepository.countByProviderId(providerId);
    }

    // ⌚ Section 2.5 & 7: Automated no-show detection
    @Scheduled(cron = "0 0/15 * * * *") // Every 15 minutes
    @Transactional
    public void detectNoShows() {
        // Logic to find appointments where appointmentDate and endTime are in the past
        // and status is still 'SCHEDULED', then update to 'NO_SHOW'
    }
}
