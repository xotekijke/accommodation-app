package com.example.accommodation.service.impl;

import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.dto.payment.PaymentRequestDto;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.exception.PaymentProcessingException;
import com.example.accommodation.mapper.PaymentMapper;
import com.example.accommodation.model.Booking;
import com.example.accommodation.model.Payment;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.PaymentStatus;
import com.example.accommodation.model.enums.Role;
import com.example.accommodation.repository.BookingRepository;
import com.example.accommodation.repository.PaymentRepository;
import com.example.accommodation.service.NotificationService;
import com.example.accommodation.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final long CENTS_PER_UNIT = 100L;
    private static final String CURRENCY = "usd";
    private static final String SESSION_ID_PARAM = "session_id";
    private static final String SESSION_ID_PLACEHOLDER = "{CHECKOUT_SESSION_ID}";

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;
    private final String stripeApiKey;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              PaymentMapper paymentMapper,
                              NotificationService notificationService,
                              @Value("${stripe.secret.key}") String stripeApiKey) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.notificationService = notificationService;
        this.stripeApiKey = stripeApiKey;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public List<PaymentDto> findByUser(User user, Long userId) {
        if (user.getRole() != Role.MANAGER && userId != null
                && !userId.equals(user.getId())) {
            throw new AccessDeniedException("You can only view your own payments");
        }
        Long targetId = user.getRole() == Role.MANAGER ? userId : user.getId();
        List<Payment> payments = targetId == null
                ? paymentRepository.findAll()
                : paymentRepository.findAllByBookingUserId(targetId);
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    @Transactional
    public PaymentDto createPaymentSession(User user, PaymentRequestDto requestDto,
                                           String successBaseUrl, String cancelBaseUrl) {
        Booking booking = getBookingOrThrow(requestDto.bookingId());
        checkPaymentAccess(user, booking);

        BigDecimal totalAmount = calculateTotalAmount(booking);
        Session session = createStripeSession(booking, totalAmount, successBaseUrl, cancelBaseUrl);
        Payment payment = buildPayment(booking, session, totalAmount);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find payment by session id " + sessionId));
        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                notificationService.sendNotification(
                        "Payment successful for booking " + payment.getBooking().getId());
            }
        } catch (StripeException e) {
            throw new PaymentProcessingException(
                    "Failed to verify Stripe session " + sessionId, e);
        }
    }

    @Override
    public void handleCancel(String sessionId) {
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find booking by id " + bookingId));
    }

    private void checkPaymentAccess(User user, Booking booking) {
        if (user.getRole() != Role.MANAGER && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only pay for your own bookings");
        }
    }

    private BigDecimal calculateTotalAmount(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return booking.getAccommodation().getDailyRate().multiply(BigDecimal.valueOf(days));
    }

    private Session createStripeSession(Booking booking, BigDecimal totalAmount,
                                        String successBaseUrl, String cancelBaseUrl) {
        long unitAmountCents = totalAmount.multiply(BigDecimal.valueOf(CENTS_PER_UNIT))
                .longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(buildRedirectUrl(successBaseUrl))
                .setCancelUrl(buildRedirectUrl(cancelBaseUrl))
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(CURRENCY)
                                .setUnitAmount(unitAmountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData
                                        .ProductData.builder()
                                        .setName("Booking #" + booking.getId())
                                        .build())
                                .build())
                        .build())
                .build();
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new PaymentProcessingException(
                    "Failed to create Stripe session for booking " + booking.getId(), e);
        }
    }

    private String buildRedirectUrl(String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam(SESSION_ID_PARAM, SESSION_ID_PLACEHOLDER)
                .build(false)
                .toUriString();
    }

    private Payment buildPayment(Booking booking, Session session, BigDecimal totalAmount) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(toUrl(session.getUrl()));
        payment.setAmountToPay(totalAmount);
        return payment;
    }

    private URL toUrl(String value) {
        try {
            return URI.create(value).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new PaymentProcessingException("Invalid Stripe session URL: " + value, e);
        }
    }
}
