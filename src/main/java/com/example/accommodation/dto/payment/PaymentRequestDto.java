package com.example.accommodation.dto.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(@NotNull Long bookingId) {
}
