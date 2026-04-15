package com.app.caresync.service;

import com.app.caresync.client.PatientClient;
import com.app.caresync.client.ProviderClient;
import com.app.caresync.dto.AppointmentRequest;
import com.app.caresync.dto.AppointmentResponse;
import com.app.caresync.dto.PatientResponse;
import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import com.app.caresync.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientClient patientClient;
    @Autowired private ProviderClient providerClient;
    @Autowired private RestTemplate restTemplate;

    @Value("${SCHEDULE_SERVICE_URL:http://localhost:8085}")
    private String scheduleServiceUrl;

    @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8089}")
    private String notificationServiceUrl;

    // PDF: bookAppointment() — marks slot as booked, sends booking confirmation
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);

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

        // PDF: On booking, call Schedule-Service to mark slot as booked
        if (request.getSlotId() != null) {
            try {
                restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + request.getSlotId() + "/book", null);
            } catch (Exception e) {
                // Log but don't fail the booking
            }
        }

        // PDF: Auto-send booking confirmation notification
        try {
            restTemplate.postForEntity(
                notificationServiceUrl + "/api/notifications/internal/booking-confirmation" +
                "?patientId=" + saved.getPatientId() +
                "&providerId=" + saved.getProviderId() +
                "&appointmentId=" + saved.getAppointmentId(),
                null, Void.class);
        } catch (Exception e) { /* non-blocking */ }

        return mapToResponse(saved);
    }

    // PDF: getByPatient()
    public List<AppointmentResponse> getMyAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);
        return appointmentRepository.findByPatientId(patient.getPatientId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getByProvider()
    public List<AppointmentResponse> getProviderSchedule(Long providerId) {
        return appointmentRepository.findByProviderId(providerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getByProviderAndDate() — today's schedule
    public List<AppointmentResponse> getProviderTodaySchedule(Long providerId) {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        return appointmentRepository.findByProviderIdAndAppointmentDateTimeBetween(providerId, start, end)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: findUpcomingByPatientId()
    public List<AppointmentResponse> getUpcomingByPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);
        return appointmentRepository.findUpcomingByPatientId(patient.getPatientId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: cancelAppointment() — releases slot, triggers refund notification
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (apt.getStatus() == AppointmentStatus.COMPLETED)
            throw new RuntimeException("Cannot cancel a completed appointment");

        apt.setStatus(AppointmentStatus.CANCELLED);
        apt.setUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(apt);

        // PDF: On cancellation, call Schedule-Service to release the slot
        if (saved.getSlotId() != null) {
            try {
                restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + saved.getSlotId() + "/release", null);
            } catch (Exception e) { /* non-blocking */ }
        }

        // PDF: Send cancellation notification
        try {
            restTemplate.postForEntity(
                notificationServiceUrl + "/api/notifications/internal/cancellation-alert" +
                "?patientId=" + saved.getPatientId() +
                "&providerId=" + saved.getProviderId() +
                "&appointmentId=" + saved.getAppointmentId(),
                null, Void.class);
        } catch (Exception e) { /* non-blocking */ }

        return mapToResponse(saved);
    }

    // PDF: rescheduleAppointment()
    public AppointmentResponse rescheduleAppointment(Long id, AppointmentRequest request) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (apt.getStatus() == AppointmentStatus.COMPLETED || apt.getStatus() == AppointmentStatus.CANCELLED)
            throw new RuntimeException("Cannot reschedule a " + apt.getStatus() + " appointment");

        // Release old slot if present
        if (apt.getSlotId() != null) {
            try { restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + apt.getSlotId() + "/release", null); }
            catch (Exception e) { /* non-blocking */ }
        }

        apt.setAppointmentDateTime(request.getAppointmentDateTime());
        apt.setSlotId(request.getSlotId());
        apt.setStatus(AppointmentStatus.RESCHEDULED);
        apt.setUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(apt);

        // Book new slot
        if (request.getSlotId() != null) {
            try { restTemplate.put(scheduleServiceUrl + "/api/slots/internal/" + request.getSlotId() + "/book", null); }
            catch (Exception e) { /* non-blocking */ }
        }

        return mapToResponse(saved);
    }

    // PDF: completeAppointment()
    public AppointmentResponse completeAppointment(Long id, String notes) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        apt.setStatus(AppointmentStatus.COMPLETED);
        apt.setDoctorNotes(notes);
        apt.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(appointmentRepository.save(apt));
    }

    // PDF: updateStatus() — generic status update
    public AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        apt.setStatus(newStatus);
        apt.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(appointmentRepository.save(apt));
    }

    // PDF (System actor): markNoShow()
    public AppointmentResponse markNoShow(Long id) {
        return updateStatus(id, AppointmentStatus.NO_SHOW);
    }

    // PDF: getById()
    public AppointmentResponse getById(Long id) {
        return appointmentRepository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    // PDF: getAllForAdmin()
    public List<AppointmentResponse> getAllForAdmin() {
        return appointmentRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getAppointmentCount()
    public long getAppointmentCount(Long providerId) {
        return appointmentRepository.countByProviderId(providerId);
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return AppointmentResponse.builder()
                .appointmentId(a.getAppointmentId()).patientId(a.getPatientId())
                .providerId(a.getProviderId()).slotId(a.getSlotId())
                .appointmentDateTime(a.getAppointmentDateTime()).status(a.getStatus())
                .reason(a.getReason()).doctorNotes(a.getDoctorNotes())
                .modeOfConsultation(a.getModeOfConsultation()).serviceType(a.getServiceType())
                .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt()).build();
    }
}
