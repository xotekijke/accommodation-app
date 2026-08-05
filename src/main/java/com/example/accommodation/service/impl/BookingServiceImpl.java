package com.example.accommodation.service.impl;

import com.example.accommodation.dto.booking.BookingDetailedDto;
import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.dto.booking.BookingRequestDto;
import com.example.accommodation.exception.BookingConflictException;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.mapper.BookingMapper;
import com.example.accommodation.model.Accommodation;
import com.example.accommodation.model.Booking;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.BookingStatus;
import com.example.accommodation.model.enums.Role;
import com.example.accommodation.repository.AccommodationRepository;
import com.example.accommodation.repository.BookingRepository;
import com.example.accommodation.repository.BookingSpecification;
import com.example.accommodation.service.BookingService;
import com.example.accommodation.service.NotificationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
            AccommodationRepository accommodationRepository,
            BookingMapper bookingMapper,
            NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.accommodationRepository = accommodationRepository;
        this.bookingMapper = bookingMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public BookingDto create(User user, BookingRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository
                .findWithLockById(requestDto.getAccommodationId());
        if (accommodation == null) {
            throw new EntityNotFoundException(
                    "Can't find accommodation by id " + requestDto.getAccommodationId());
        }
        if (accommodation.getAvailability() <= 0) {
            throw new BookingConflictException(
                    "Accommodation " + accommodation.getId() + " has no available units");
        }
        boolean overlaps = bookingRepository.existsOverlappingBooking(
                accommodation.getId(), requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        if (overlaps) {
            throw new BookingConflictException(
                    "Accommodation " + accommodation.getId()
                            + " is already booked for the requested dates");
        }
        Booking booking = new Booking();
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);

        accommodation.setAvailability(accommodation.getAvailability() - 1);
        accommodationRepository.save(accommodation);
        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(
                "New booking created: accommodation " + accommodation.getId()
                        + " from " + booking.getCheckInDate()
                        + " to " + booking.getCheckOutDate());
        return bookingMapper.toDto(saved);
    }

    @Override
    public List<BookingDto> search(Long userId, BookingStatus status) {
        Specification<Booking> spec = Specification
                .where(BookingSpecification.hasUserId(userId))
                .and(BookingSpecification.hasStatus(status));
        return bookingRepository.findAll(spec).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> findMyBookings(User user) {
        return bookingRepository.findAllByUserId(user.getId()).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDetailedDto findById(User user, Long id) {
        Booking booking = getBookingOrThrow(id);
        checkOwnership(user, booking);
        return bookingMapper.toDetailedDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(User user, Long id, BookingRequestDto requestDto) {
        Booking booking = getBookingOrThrow(id);
        checkOwnership(user, booking);
        boolean overlaps = bookingRepository.existsOverlappingBooking(
                booking.getAccommodation().getId(),
                requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        if (overlaps && (!requestDto.getCheckInDate().equals(booking.getCheckInDate())
                || !requestDto.getCheckOutDate().equals(booking.getCheckOutDate()))) {
            throw new BookingConflictException(
                    "Accommodation " + booking.getAccommodation().getId()
                            + " is already booked for the requested dates");
        }
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void cancel(User user, Long id) {
        Booking booking = getBookingOrThrow(id);
        checkOwnership(user, booking);
        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BookingConflictException("Booking " + id + " is already canceled");
        }
        booking.setStatus(BookingStatus.CANCELED);
        Accommodation accommodation = booking.getAccommodation();
        accommodation.setAvailability(accommodation.getAvailability() + 1);
        accommodationRepository.save(accommodation);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                "Booking canceled: accommodation " + accommodation.getId()
                        + ", booking id " + booking.getId());
    }

    @Override
    @Transactional
    public void expireOverdueBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Booking> expired = bookingRepository
                .findAllByStatusNotAndCheckOutDateLessThanEqual(
                        BookingStatus.CANCELED, tomorrow).stream()
                .filter(b -> b.getStatus() != BookingStatus.EXPIRED)
                .toList();
        if (expired.isEmpty()) {
            notificationService.sendNotification("No expired bookings today!");
            return;
        }
        for (Booking booking : expired) {
            booking.setStatus(BookingStatus.EXPIRED);
            Accommodation accommodation = booking.getAccommodation();
            accommodation.setAvailability(accommodation.getAvailability() + 1);
            accommodationRepository.save(accommodation);
            bookingRepository.save(booking);
            notificationService.sendNotification(
                    "Booking expired: accommodation " + accommodation.getId()
                            + ", booking id " + booking.getId()
                            + ", was due " + booking.getCheckOutDate());
        }
    }

    private Booking getBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find booking by id " + id));
    }

    private void checkOwnership(User user, Booking booking) {
        boolean isManager = user.getRole() == Role.MANAGER;
        if (!isManager && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only access your own bookings");
        }
    }
}
