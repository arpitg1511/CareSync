package com.app.caresync.repository;

import com.app.caresync.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByProviderId(Long providerId);
    List<AvailabilitySlot> findByProviderIdAndDate(Long providerId, LocalDate date);
    
    @Query("SELECT s FROM AvailabilitySlot s WHERE s.providerId = ?1 AND s.date = ?2 AND s.isBooked = false AND s.isBlocked = false")
    List<AvailabilitySlot> findAvailableByProviderAndDate(Long providerId, LocalDate date);
    
    List<AvailabilitySlot> findByDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT COUNT(s) FROM AvailabilitySlot s WHERE s.providerId = ?1 AND s.isBooked = false AND s.isBlocked = false")
    long countAvailableByProviderId(Long providerId);
    
    void deleteBySlotId(Long slotId);
}
