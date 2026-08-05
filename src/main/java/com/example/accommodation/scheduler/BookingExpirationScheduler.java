package com.example.accommodation.scheduler;

import com.example.accommodation.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingExpirationScheduler {
    private final BookingService bookingService;

    public BookingExpirationScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(cron = "${booking.expiration.cron:0 0 1 * * *}")
    public void expireOverdueBookings() {
        bookingService.expireOverdueBookings();
    }
}
