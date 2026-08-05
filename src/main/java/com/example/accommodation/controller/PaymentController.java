package com.example.accommodation.controller;

import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.dto.payment.PaymentRequestDto;
import com.example.accommodation.model.User;
import com.example.accommodation.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Endpoints for handling Stripe payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentDto> findByUser(@AuthenticationPrincipal User user,
                                       @RequestParam(required = false) Long userId) {
        return paymentService.findByUser(user, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto createSession(@AuthenticationPrincipal User user,
                                    @RequestBody @Valid PaymentRequestDto requestDto,
                                    HttpServletRequest request) {
        // Replaced fromHttpUrl with fromUriString
        String successUrl = UriComponentsBuilder.fromUriString(baseUrl(request))
                .path("/payments/success").toUriString();

        String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl(request))
                .path("/payments/cancel").toUriString();

        return paymentService.createPaymentSession(user, requestDto, successUrl, cancelUrl);
    }

    @GetMapping("/success")
    public Map<String, String> success(@RequestParam("session_id") String sessionId) {
        paymentService.handleSuccess(sessionId);
        return Map.of("message", "Payment was successful");
    }

    @GetMapping("/cancel")
    public Map<String, String> cancel(@RequestParam("session_id") String sessionId) {
        paymentService.handleCancel(sessionId);
        return Map.of("message",
                "Payment was paused. You can complete it later; the session stays "
                        + "available for 24 hours.");
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443
                ? "" : ":" + request.getServerPort());
    }
}
