package com.app.caresync.service;

import com.app.caresync.dto.RecordRequest;
import com.app.caresync.dto.RecordResponse;

import java.util.List;

public interface RecordService {
    RecordResponse createRecord(RecordRequest request);
    RecordResponse getRecordByAppointment(Long appointmentId);
    List<RecordResponse> getRecordsByPatient(Long patientId);
    List<RecordResponse> getRecordsByProvider(Long providerId);
    List<RecordResponse> getAllRecords();
    RecordResponse getRecordById(Long recordId);
    RecordResponse updateRecord(Long recordId, RecordRequest request);
    void deleteRecord(Long recordId);
    List<RecordResponse> getFollowUpRecords();
    long getRecordCount(Long patientId);
    RecordResponse attachDocument(Long recordId, String attachmentUrl);
}
