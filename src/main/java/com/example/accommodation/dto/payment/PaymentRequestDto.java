package com.example.accommodation.dto.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentRequestDto {
    @NotNull
    private Long bookingId;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}
