package com.example.accommodation.service;

import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.dto.payment.PaymentRequestDto;
import com.example.accommodation.model.User;
import java.util.List;

public interface PaymentService {

    List<PaymentDto> findByUser(User user, Long userId);

    PaymentDto createPaymentSession(User user, PaymentRequestDto requestDto,
            String successBaseUrl, String cancelBaseUrl);

    void handleSuccess(String sessionId);

    void handleCancel(String sessionId);
}
