package com.example.accommodation.mapper;

import com.example.accommodation.config.MapperConfig;
import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import com.example.accommodation.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {

    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toModel(AccommodationRequestDto requestDto);

    void updateModel(AccommodationRequestDto requestDto,
                     @MappingTarget Accommodation accommodation);
}
