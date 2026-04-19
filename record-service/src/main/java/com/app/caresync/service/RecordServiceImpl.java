package com.app.caresync.service;

import com.app.caresync.dto.RecordRequest;
import com.app.caresync.dto.RecordResponse;
import com.app.caresync.exception.RecordNotFoundException;
import com.app.caresync.model.MedicalRecord;
import com.app.caresync.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Override
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToResponse(recordRepository.save(record));
    }

    @Override
    public RecordResponse getRecordByAppointment(Long appointmentId) {
        return recordRepository.findByAppointmentId(appointmentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RecordNotFoundException("Record not found for appointment " + appointmentId));
    }

    @Override
    public List<RecordResponse> getRecordsByPatient(Long patientId) {
        return recordRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getRecordsByProvider(Long providerId) {
        return recordRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public RecordResponse getRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with id: " + recordId));
    }

    @Override
    public RecordResponse updateRecord(Long recordId, RecordRequest request) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with id: " + recordId));

        if (request.getDiagnosis() != null) record.setDiagnosis(request.getDiagnosis());
        if (request.getPrescription() != null) record.setPrescription(request.getPrescription());
        if (request.getNotes() != null) record.setNotes(request.getNotes());
        if (request.getAttachmentUrl() != null) record.setAttachmentUrl(request.getAttachmentUrl());
        if (request.getFollowUpDate() != null) record.setFollowUpDate(request.getFollowUpDate());
        record.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(recordRepository.save(record));
    }

    @Override
    public void deleteRecord(Long recordId) {
        if (!recordRepository.existsById(recordId)) {
            throw new RecordNotFoundException("Record not found with id: " + recordId);
        }
        recordRepository.deleteById(recordId);
    }

    @Override
    public List<RecordResponse> getFollowUpRecords() {
        LocalDate today = LocalDate.now();
        return recordRepository.findByFollowUpDate(today).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public long getRecordCount(Long patientId) {
        return recordRepository.countByPatientId(patientId);
    }

    @Override
    public RecordResponse attachDocument(Long recordId, String attachmentUrl) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with id: " + recordId));
        record.setAttachmentUrl(attachmentUrl);
        record.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(recordRepository.save(record));
    }

    private RecordResponse mapToResponse(MedicalRecord r) {
        if (r == null) return null;
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
}
