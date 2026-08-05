package com.example.accommodation.repository;

import com.example.accommodation.model.Booking;
import com.example.accommodation.model.enums.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

public final class BookingSpecification {

    private BookingSpecification() {
    }

    public static Specification<Booking> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null
                ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) -> status == null
                ? null : cb.equal(root.get("status"), status);
    }
}
