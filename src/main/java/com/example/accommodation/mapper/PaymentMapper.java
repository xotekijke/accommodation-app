package com.example.accommodation.mapper;

import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setStatus(payment.getStatus());
        dto.setBookingId(payment.getBooking().getId());
        dto.setSessionUrl(payment.getSessionUrl() == null
                ? null : payment.getSessionUrl().toString());
        dto.setSessionId(payment.getSessionId());
        dto.setAmountToPay(payment.getAmountToPay());
        return dto;
    }
}
