package com.example.accommodation.mapper;

import com.example.accommodation.dto.booking.BookingDetailedDto;
import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    private final AccommodationMapper accommodationMapper;

    public BookingMapper(AccommodationMapper accommodationMapper) {
        this.accommodationMapper = accommodationMapper;
    }

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setAccommodationId(booking.getAccommodation().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public BookingDetailedDto toDetailedDto(Booking booking) {
        BookingDetailedDto dto = new BookingDetailedDto();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setAccommodation(accommodationMapper.toDto(booking.getAccommodation()));
        dto.setUserId(booking.getUser().getId());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
