package com.example.accommodation.service;

import com.example.accommodation.dto.booking.BookingDetailedDto;
import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.dto.booking.BookingRequestDto;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.BookingStatus;
import java.util.List;

public interface BookingService {

    BookingDto create(User user, BookingRequestDto requestDto);

    List<BookingDto> search(Long userId, BookingStatus status);

    List<BookingDto> findMyBookings(User user);

    BookingDetailedDto findById(User user, Long id);

    BookingDto update(User user, Long id, BookingRequestDto requestDto);

    void cancel(User user, Long id);

    void expireOverdueBookings();
}
