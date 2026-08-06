package com.example.accommodation.dto.accommodation;

import com.example.accommodation.model.enums.AccommodationType;
import java.math.BigDecimal;
import java.util.List;

public record AccommodationDto(
        Long id,
        AccommodationType type,
        String location,
        String size,
        List<String> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
