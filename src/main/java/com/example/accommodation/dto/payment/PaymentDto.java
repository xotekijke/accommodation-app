package com.example.accommodation.dto.payment;

import com.example.accommodation.model.enums.PaymentStatus;
import java.math.BigDecimal;

public record PaymentDto(
        Long id,
        PaymentStatus status,
        Long bookingId,
        String sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {
}
