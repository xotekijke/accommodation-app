package com.example.accommodation.repository;

import com.example.accommodation.model.Booking;
import com.example.accommodation.model.enums.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    List<Booking> findAllByUserId(Long userId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status NOT IN (com.example.accommodation.model.enums.BookingStatus.CANCELED, "
            + "com.example.accommodation.model.enums.BookingStatus.EXPIRED) "
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    boolean existsOverlappingBooking(@Param("accommodationId") Long accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    List<Booking> findAllByStatusNotAndCheckOutDateLessThanEqual(
            BookingStatus status, LocalDate date);
}
