package com.app.caresync.service;

import com.app.caresync.dto.RecordRequest;
import com.app.caresync.dto.RecordResponse;
import com.app.caresync.model.MedicalRecord;
import com.app.caresync.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    public RecordResponse createRecord(RecordRequest request) {
        if (recordRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new RuntimeException("Medical record already exists for this appointment");
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .notes(request.getNotes())
                .attachmentUrl(request.getAttachmentUrl())
                .followUpDate(request.getFollowUpDate())
                .build();

        return mapToResponse(recordRepository.save(record));
    }

    public RecordResponse getRecordByAppointment(Long appointmentId) {
        return recordRepository.findByAppointmentId(appointmentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Record not found for appointment " + appointmentId));
    }

    public List<RecordResponse> getRecordsByPatient(Long patientId) {
        return recordRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<RecordResponse> getRecordsByProvider(Long providerId) {
        return recordRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<RecordResponse> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public RecordResponse getRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public RecordResponse updateRecord(Long recordId, RecordRequest request) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (request.getDiagnosis() != null) record.setDiagnosis(request.getDiagnosis());
        if (request.getPrescription() != null) record.setPrescription(request.getPrescription());
        if (request.getNotes() != null) record.setNotes(request.getNotes());
        if (request.getAttachmentUrl() != null) record.setAttachmentUrl(request.getAttachmentUrl());
        if (request.getFollowUpDate() != null) record.setFollowUpDate(request.getFollowUpDate());
        record.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(recordRepository.save(record));
    }

    public void deleteRecord(Long recordId) {
        recordRepository.deleteById(recordId);
    }

    private RecordResponse mapToResponse(MedicalRecord r) {
        return RecordResponse.builder()
                .recordId(r.getRecordId())
                .appointmentId(r.getAppointmentId())
                .patientId(r.getPatientId())
                .providerId(r.getProviderId())
                .diagnosis(r.getDiagnosis())
                .prescription(r.getPrescription())
                .notes(r.getNotes())
                .attachmentUrl(r.getAttachmentUrl())
                .followUpDate(r.getFollowUpDate())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
    // PDF: getFollowUpRecords() — records with a follow-up date
    public List<RecordResponse> getFollowUpRecords() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return recordRepository.findByFollowUpDate(today).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getRecordCount() for a patient
    public long getRecordCount(Long patientId) {
        return recordRepository.countByPatientId(patientId);
    }

    // PDF: attachDocument() — update only the attachment URL
    public RecordResponse attachDocument(Long recordId, String attachmentUrl) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        record.setAttachmentUrl(attachmentUrl);
        record.setUpdatedAt(java.time.LocalDateTime.now());
        return mapToResponse(recordRepository.save(record));
    }


}

