package com.example.accommodation.mapper;

import com.example.accommodation.config.MapperConfig;
import com.example.accommodation.dto.booking.BookingDetailedDto;
import com.example.accommodation.dto.booking.BookingDto;
import com.example.accommodation.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = AccommodationMapper.class)
public interface BookingMapper {

    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "userId", source = "user.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "userId", source = "user.id")
    BookingDetailedDto toDetailedDto(Booking booking);
}
