package com.example.accommodation.dto.booking;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.model.enums.BookingStatus;
import java.time.LocalDate;

public record BookingDetailedDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        AccommodationDto accommodation,
        Long userId,
        BookingStatus status
) {
}

