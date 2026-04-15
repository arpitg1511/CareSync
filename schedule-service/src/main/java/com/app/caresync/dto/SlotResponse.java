package com.app.caresync.dto;

import com.app.caresync.model.Recurrence;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
public class SlotResponse {
    private Long slotId;
    private Long providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Boolean isBooked;
    private Boolean isBlocked;
    private Recurrence recurrence;
    private LocalDateTime createdAt;
}
