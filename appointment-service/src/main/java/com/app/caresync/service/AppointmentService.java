package com.app.caresync.service;

import com.app.caresync.dto.AppointmentRequest;
import com.app.caresync.dto.AppointmentResponse;
import com.app.caresync.model.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentRequest request);
    List<AppointmentResponse> getMyAppointments();
    List<AppointmentResponse> getProviderSchedule(Long providerId);
    List<AppointmentResponse> getProviderTodaySchedule(Long providerId);
    List<AppointmentResponse> getUpcomingByPatient();
    AppointmentResponse cancelAppointment(Long id);
    AppointmentResponse rescheduleAppointment(Long id, AppointmentRequest request);
    AppointmentResponse completeAppointment(Long id, String notes);
    AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus);
    AppointmentResponse markNoShow(Long id);
    AppointmentResponse getById(Long id);
    List<AppointmentResponse> getAllForAdmin();
    long getAppointmentCount(Long providerId);
}
