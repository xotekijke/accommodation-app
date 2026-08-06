package com.example.accommodation.controller;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import com.example.accommodation.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
@Tag(name = "Accommodations", description = "Endpoints for managing accommodation inventory")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create accommodation",
            description = "Creates a new accommodation. Manager role required.")
    public AccommodationDto create(@RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.create(requestDto);
    }

    @GetMapping
    @Operation(summary = "List accommodations",
            description = "Returns all available accommodations.")
    public List<AccommodationDto> findAll() {
        return accommodationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by id")
    public AccommodationDto findById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update accommodation",
            description = "Fully replaces an accommodation. Manager role required.")
    public AccommodationDto update(@PathVariable Long id,
                                   @RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.update(id, requestDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Partially update accommodation", description = "Manager role required.")
    public AccommodationDto patch(@PathVariable Long id,
                                  @RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete accommodation",
            description = "Soft-deletes an accommodation. Manager role required.")
    public void delete(@PathVariable Long id) {
        accommodationService.delete(id);
    }
}
