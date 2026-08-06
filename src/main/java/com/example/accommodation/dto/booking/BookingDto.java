package com.example.accommodation.dto.booking;

import com.example.accommodation.model.enums.BookingStatus;
import java.time.LocalDate;

public record BookingDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Long userId,
        BookingStatus status
) {
}
