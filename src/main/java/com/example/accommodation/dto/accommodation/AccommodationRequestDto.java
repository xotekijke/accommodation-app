package com.example.accommodation.dto.accommodation;

import com.example.accommodation.model.enums.AccommodationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

public record AccommodationRequestDto(
        @NotNull AccommodationType type,
        @NotBlank String location,
        @NotBlank String size,
        List<String> amenities,
        @NotNull @Positive BigDecimal dailyRate,
        @NotNull @PositiveOrZero Integer availability
) {
}
