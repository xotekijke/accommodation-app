package com.example.accommodation.controller;

import com.example.accommodation.dto.booking.BookingDetailedDto;
import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.dto.booking.BookingRequestDto;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.BookingStatus;
import com.example.accommodation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Endpoints for managing accommodation bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create booking")
    public BookingDto create(@AuthenticationPrincipal User user,
                             @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.create(user, requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Search bookings", description = "Manager role required.")
    public List<BookingDto> search(@RequestParam(required = false) Long userId,
                                   @RequestParam(required = false) BookingStatus status) {
        return bookingService.search(userId, status);
    }

    @GetMapping("/my")
    @Operation(summary = "Get my bookings")
    public List<BookingDto> findMyBookings(@AuthenticationPrincipal User user) {
        return bookingService.findMyBookings(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by id")
    public BookingDetailedDto findById(@AuthenticationPrincipal User user,
                                       @PathVariable Long id) {
        return bookingService.findById(user, id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking")
    public BookingDto update(@AuthenticationPrincipal User user,
                             @PathVariable Long id,
                             @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.update(user, id, requestDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update booking")
    public BookingDto patch(@AuthenticationPrincipal User user,
                            @PathVariable Long id,
                            @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.update(user, id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel booking")
    public void cancel(@AuthenticationPrincipal User user, @PathVariable Long id) {
        bookingService.cancel(user, id);
    }
}
