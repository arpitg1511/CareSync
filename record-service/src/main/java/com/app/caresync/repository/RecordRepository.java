package com.app.caresync.repository;

import com.app.caresync.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<MedicalRecord> findByProviderId(Long providerId);
    List<MedicalRecord> findByFollowUpDate(LocalDate date);
    long countByPatientId(Long patientId);
    void deleteByRecordId(Long recordId);
}
