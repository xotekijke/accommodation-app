package com.example.accommodation.repository;

import com.example.accommodation.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByBookingUserId(Long userId);

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findBySessionId(String sessionId);
}
