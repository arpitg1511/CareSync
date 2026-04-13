package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    private Long providerId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer durationMinutes;

    @Builder.Default
    private Boolean isBooked = false;

    @Builder.Default
    private Boolean isBlocked = false;

    private String recurrence;

    @Version
    private Integer version; // Optimistic locking to prevent double-booking

    private LocalDateTime createdAt = LocalDateTime.now();
}
