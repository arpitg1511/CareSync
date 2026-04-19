package com.app.caresync.repository;

import com.app.caresync.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    // PDF: findByProviderId()
    List<AvailabilitySlot> findByProviderId(Long providerId);
    List<AvailabilitySlot> findByProviderIdAndDate(Long providerId, LocalDate date);

    @Modifying
    @Transactional
    @Query("DELETE FROM AvailabilitySlot s WHERE s.providerId = :providerId AND s.date = :date")
    void deleteByProviderIdAndDate(Long providerId, LocalDate date);

    // PDF: findByProviderIdAndDate()
//    List<AvailabilitySlot> findByProviderIdAndDate(Long providerId, LocalDate date);

    // PDF: findAvailableByProviderAndDate() - only unbooked, unblocked slots visible to patients
    @Query("SELECT s FROM AvailabilitySlot s WHERE s.providerId = :providerId AND s.date = :date " +
           "AND s.isBooked = false AND s.isBlocked = false")
    List<AvailabilitySlot> findAvailableByProviderAndDate(Long providerId, LocalDate date);

    // PDF: findByDateBetween()
    List<AvailabilitySlot> findByDateBetween(LocalDate start, LocalDate end);

    // PDF: countAvailableByProviderId()
    @Query("SELECT COUNT(s) FROM AvailabilitySlot s WHERE s.providerId = :providerId " +
           "AND s.isBooked = false AND s.isBlocked = false")
    Long countAvailableByProviderId(Long providerId);

    // Upcoming unbooked slots for a provider
    @Query("SELECT s FROM AvailabilitySlot s WHERE s.providerId = :providerId " +
           "AND s.isBooked = false AND s.isBlocked = false AND s.date >= CURRENT_DATE")
    List<AvailabilitySlot> findUpcomingAvailableByProvider(Long providerId);

    // PDF: Expired unbooked slots for purge scheduler
    @Query("SELECT s FROM AvailabilitySlot s WHERE s.date < :today AND s.isBooked = false")
    List<AvailabilitySlot> findExpiredUnbookedSlots(LocalDate today);
}
