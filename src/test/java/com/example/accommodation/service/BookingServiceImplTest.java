package com.example.accommodation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.dto.booking.BookingRequestDto;
import com.example.accommodation.exception.BookingConflictException;
import com.example.accommodation.mapper.BookingMapper;
import com.example.accommodation.model.Accommodation;
import com.example.accommodation.model.Booking;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.BookingStatus;
import com.example.accommodation.repository.AccommodationRepository;
import com.example.accommodation.repository.BookingRepository;
import com.example.accommodation.service.impl.BookingServiceImpl;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private static final Long ACCOMMODATION_ID = 1L;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private NotificationService notificationService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository, accommodationRepository, bookingMapper, notificationService);
    }

    @Test
    void create_noAvailability_throwsConflict() {
        User user = new User();
        user.setId(2L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(ACCOMMODATION_ID);
        accommodation.setAvailability(0);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationId(ACCOMMODATION_ID);
        requestDto.setCheckInDate(LocalDate.now());
        requestDto.setCheckOutDate(LocalDate.now().plusDays(2));

        when(accommodationRepository.findWithLockById(ACCOMMODATION_ID))
                .thenReturn(accommodation);

        assertThatThrownBy(() -> bookingService.create(user, requestDto))
                .isInstanceOf(BookingConflictException.class);
    }

    @Test
    void create_overlappingDates_throwsConflict() {
        User user = new User();
        user.setId(2L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(ACCOMMODATION_ID);
        accommodation.setAvailability(1);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationId(ACCOMMODATION_ID);
        requestDto.setCheckInDate(LocalDate.now());
        requestDto.setCheckOutDate(LocalDate.now().plusDays(2));

        when(accommodationRepository.findWithLockById(ACCOMMODATION_ID))
                .thenReturn(accommodation);
        when(bookingRepository.existsOverlappingBooking(
                any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.create(user, requestDto))
                .isInstanceOf(BookingConflictException.class);
    }

    @Test
    void create_validRequest_decrementsAvailabilityAndSaves() {
        User user = new User();
        user.setId(2L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(ACCOMMODATION_ID);
        accommodation.setAvailability(2);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationId(ACCOMMODATION_ID);
        requestDto.setCheckInDate(LocalDate.now());
        requestDto.setCheckOutDate(LocalDate.now().plusDays(2));

        Booking savedBooking = new Booking();
        savedBooking.setId(5L);
        savedBooking.setAccommodation(accommodation);
        savedBooking.setUser(user);
        savedBooking.setStatus(BookingStatus.PENDING);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(5L);

        when(accommodationRepository.findWithLockById(ACCOMMODATION_ID))
                .thenReturn(accommodation);
        when(bookingRepository.existsOverlappingBooking(any(), any(), any()))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.toDto(savedBooking)).thenReturn(expectedDto);
        doNothing().when(notificationService).sendNotification(any());

        BookingDto actual = bookingService.create(user, requestDto);

        assertThat(actual.getId()).isEqualTo(5L);
        assertThat(accommodation.getAvailability()).isEqualTo(1);
    }
}