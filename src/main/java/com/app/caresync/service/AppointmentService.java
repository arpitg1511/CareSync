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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private ProviderClient providerClient;

    // 📅 Book a new Appointment
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);

        // Verify Provider exists
        if (!providerClient.checkIfProviderExists(request.getProviderId())) {
            throw new RuntimeException("Error: Provider not found or inactive.");
        }

        Appointment appointment = Appointment.builder()
                .patientId(patient.getPatientId())
                .providerId(request.getProviderId())
                .appointmentDateTime(request.getAppointmentDateTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.getReason())
                .build();

        return mapToResponse(appointmentRepository.save(appointment));
    }

    // 👤 Get Patient History
    public List<AppointmentResponse> getMyAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponse patient = patientClient.getPatientByEmail(email);
        
        return appointmentRepository.findByPatientId(patient.getPatientId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 👨‍⚕️ Get Provider Schedule
    public List<AppointmentResponse> getProviderSchedule(Long providerId) {
        return appointmentRepository.findByProviderId(providerId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🚫 Cancel Appointment (Patient only)
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        
        // Logical check: only cancel if still scheduled
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Error: Only scheduled appointments can be cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return mapToResponse(appointmentRepository.save(appointment));
    }

    // ✅ Complete Appointment (Doctor only)
    public AppointmentResponse completeAppointment(Long appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDoctorNotes(notes);
        return mapToResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return AppointmentResponse.builder()
                .appointmentId(a.getAppointmentId())
                .patientId(a.getPatientId())
                .providerId(a.getProviderId())
                .appointmentDateTime(a.getAppointmentDateTime())
                .status(a.getStatus())
                .reason(a.getReason())
                .doctorNotes(a.getDoctorNotes())
                .build();
    }
}
