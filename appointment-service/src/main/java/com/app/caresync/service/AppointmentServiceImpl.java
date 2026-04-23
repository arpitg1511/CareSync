package com.app.caresync.service;

import com.app.caresync.client.PatientClient;
import com.app.caresync.client.ProviderClient;
import com.app.caresync.dto.AppointmentRequest;
import com.app.caresync.dto.AppointmentResponse;
import com.app.caresync.dto.PatientResponse;
import com.app.caresync.exception.AppointmentNotFoundException;
import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import com.app.caresync.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientClient patientClient;
    @Autowired private ProviderClient providerClient;
    @Autowired private RestTemplate restTemplate;
    @Autowired private MessageProducer messageProducer;

    @Value("${schedule.service.url}")
    private String scheduleServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Override
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient;
        try {
            patient = patientClient.getPatientByEmail(email);
        } catch (Exception e) {
            e.printStackTrace(); // 🔍 ADDED FOR DEBUGGING
            throw new RuntimeException("Operational Error: No Patient Profile associated with this account. Only verified patients can initialize booking vectors.");
        }

        if (!providerClient.checkIfProviderExists(request.getProviderId())) {
            throw new RuntimeException("Provider not found or not yet verified.");
        }

        Appointment apt = Appointment.builder()
                .patientId(patient.getPatientId())
                .providerId(request.getProviderId())
                .slotId(request.getSlotId())
                .appointmentDateTime(request.getAppointmentDateTime() != null
                        ? request.getAppointmentDateTime() : LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.getReason())
                .modeOfConsultation(request.getModeOfConsultation() != null
                        ? request.getModeOfConsultation() : "IN_PERSON")
                .serviceType(request.getServiceType())
                .build();

        Appointment saved = appointmentRepository.save(apt);

        // Notify Schedule-Service
        if (request.getSlotId() != null) {
            try {
                restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + request.getSlotId() + "/book", null);
            } catch (Exception e) { /* log and ignore */ }
        }

        // 🚀 RabbitMQ: Send confirmation asynchronously
        try {
            com.app.caresync.dto.NotificationEvent event = com.app.caresync.dto.NotificationEvent.builder()
                    .recipientId(saved.getPatientId()).recipientRole("PATIENT")
                    .title("Appointment Confirmed")
                    .message("Your appointment #" + saved.getAppointmentId() + " has been confirmed.")
                    .type("BOOKING").channel("APP")
                    .relatedId(saved.getAppointmentId()).relatedType("APPOINTMENT")
                    .build();
            messageProducer.sendNotification(event);

            com.app.caresync.dto.NotificationEvent doctorEvent = com.app.caresync.dto.NotificationEvent.builder()
                    .recipientId(saved.getProviderId()).recipientRole("DOCTOR")
                    .title("New Appointment Booked")
                    .message("New appointment #" + saved.getAppointmentId() + " has been booked.")
                    .type("BOOKING").channel("APP")
                    .relatedId(saved.getAppointmentId()).relatedType("APPOINTMENT")
                    .build();
            messageProducer.sendNotification(doctorEvent);
        } catch (Exception e) { /* log and ignore RabbitMQ errors */ }

        return mapToResponse(saved);
    }

    @Override
    public List<AppointmentResponse> getMyAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            PatientResponse patient = patientClient.getPatientByEmail(email);
            if (patient == null) return List.of();
            return appointmentRepository.findByPatientId(patient.getPatientId())
                    .stream().map(this::mapToResponse).collect(Collectors.toList());
        } catch (Exception e) {
            // If user has no patient profile (like Admin/Doctor), just return empty list
            return List.of();
        }
    }

    @Override
    public List<AppointmentResponse> getProviderSchedule(Long providerId) {
        return appointmentRepository.findByProviderId(providerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getProviderTodaySchedule(Long providerId) {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        return appointmentRepository.findByProviderIdAndAppointmentDateTimeBetween(providerId, start, end)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getUpcomingByPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);
        return appointmentRepository.findUpcomingByPatientId(patient.getPatientId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (apt.getStatus() == AppointmentStatus.COMPLETED)
            throw new RuntimeException("Cannot cancel a completed appointment");

        apt.setStatus(AppointmentStatus.CANCELLED);
        apt.setUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(apt);

        // Release slot
        if (saved.getSlotId() != null) {
            try {
                restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + saved.getSlotId() + "/release", null);
            } catch (Exception e) { /* log and ignore */ }
        }

        // RabbitMQ: Send cancellation alert asynchronously
        try {
            com.app.caresync.dto.NotificationEvent pEvent = com.app.caresync.dto.NotificationEvent.builder()
                    .recipientId(saved.getPatientId()).recipientRole("PATIENT")
                    .title("Appointment Cancelled")
                    .message("Appointment #" + saved.getAppointmentId() + " cancelled. Refund will be processed if eligible.")
                    .type("CANCELLATION").channel("APP")
                    .relatedId(saved.getAppointmentId()).relatedType("APPOINTMENT")
                    .build();
            messageProducer.sendNotification(pEvent);

            com.app.caresync.dto.NotificationEvent dEvent = com.app.caresync.dto.NotificationEvent.builder()
                    .recipientId(saved.getProviderId()).recipientRole("DOCTOR")
                    .title("Appointment Cancelled")
                    .message("Appointment #" + saved.getAppointmentId() + " was cancelled by the patient.")
                    .type("CANCELLATION").channel("APP")
                    .relatedId(saved.getAppointmentId()).relatedType("APPOINTMENT")
                    .build();
            messageProducer.sendNotification(dEvent);
        } catch (Exception e) { /* log and ignore RabbitMQ errors */ }

        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse rescheduleAppointment(Long id, AppointmentRequest request) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (apt.getStatus() == AppointmentStatus.COMPLETED || apt.getStatus() == AppointmentStatus.CANCELLED)
            throw new RuntimeException("Cannot reschedule a " + apt.getStatus() + " appointment");

        if (apt.getSlotId() != null) {
            try { restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + apt.getSlotId() + "/release", null); }
            catch (Exception e) { /* log and ignore */ }
        }

        apt.setAppointmentDateTime(request.getAppointmentDateTime());
        apt.setSlotId(request.getSlotId());
        apt.setStatus(AppointmentStatus.RESCHEDULED);
        apt.setUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(apt);

        if (request.getSlotId() != null) {
            try { restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + request.getSlotId() + "/book", null); }
            catch (Exception e) { /* log and ignore */ }
        }

        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse completeAppointment(Long id, String notes) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        apt.setStatus(AppointmentStatus.COMPLETED);
        apt.setDoctorNotes(notes);
        apt.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(appointmentRepository.save(apt));
    }

    @Override
    public AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        apt.setStatus(newStatus);
        apt.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(appointmentRepository.save(apt));
    }

    @Override
    public AppointmentResponse markNoShow(Long id) {
        return updateStatus(id, AppointmentStatus.NO_SHOW);
    }

    @Override
    public AppointmentResponse getById(Long id) {
        return appointmentRepository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
    }

    @Override
    public Map<String, Long> getAppointmentStats(Long providerId) {
        return Map.of(
            "total", appointmentRepository.countByProviderId(providerId),
            "completed", appointmentRepository.countByProviderIdAndStatus(providerId, AppointmentStatus.COMPLETED),
            "noShow", appointmentRepository.countByProviderIdAndStatus(providerId, AppointmentStatus.NO_SHOW)
        );
    }

    @Override
    public List<AppointmentResponse> getAllForAdmin() {
        return appointmentRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        if (a == null) return null;
        return AppointmentResponse.builder()
                .appointmentId(a.getAppointmentId()).patientId(a.getPatientId())
                .providerId(a.getProviderId()).slotId(a.getSlotId())
                .appointmentDateTime(a.getAppointmentDateTime()).status(a.getStatus())
                .reason(a.getReason()).doctorNotes(a.getDoctorNotes())
                .modeOfConsultation(a.getModeOfConsultation()).serviceType(a.getServiceType())
                .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt()).build();
    }
}
