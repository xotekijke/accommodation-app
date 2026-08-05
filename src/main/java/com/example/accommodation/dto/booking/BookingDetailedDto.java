package com.example.accommodation.dto.booking;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.model.enums.BookingStatus;
import java.time.LocalDate;

public class BookingDetailedDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private AccommodationDto accommodation;
    private Long userId;
    private BookingStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public AccommodationDto getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(AccommodationDto accommodation) {
        this.accommodation = accommodation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
