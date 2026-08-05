package com.example.accommodation.mapper;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import com.example.accommodation.model.Accommodation;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class AccommodationMapper {

    public Accommodation toModel(AccommodationRequestDto requestDto) {
        Accommodation accommodation = new Accommodation();
        accommodation.setType(requestDto.getType());
        accommodation.setLocation(requestDto.getLocation());
        accommodation.setSize(requestDto.getSize());
        accommodation.setAmenities(requestDto.getAmenities() == null
                ? new ArrayList<>() : requestDto.getAmenities());
        accommodation.setDailyRate(requestDto.getDailyRate());
        accommodation.setAvailability(requestDto.getAvailability());
        return accommodation;
    }

    public void updateModel(Accommodation accommodation, AccommodationRequestDto requestDto) {
        accommodation.setType(requestDto.getType());
        accommodation.setLocation(requestDto.getLocation());
        accommodation.setSize(requestDto.getSize());
        accommodation.setAmenities(requestDto.getAmenities() == null
                ? new ArrayList<>() : requestDto.getAmenities());
        accommodation.setDailyRate(requestDto.getDailyRate());
        accommodation.setAvailability(requestDto.getAvailability());
    }

    public AccommodationDto toDto(Accommodation accommodation) {
        AccommodationDto dto = new AccommodationDto();
        dto.setId(accommodation.getId());
        dto.setType(accommodation.getType());
        dto.setLocation(accommodation.getLocation());
        dto.setSize(accommodation.getSize());
        dto.setAmenities(accommodation.getAmenities());
        dto.setDailyRate(accommodation.getDailyRate());
        dto.setAvailability(accommodation.getAvailability());
        return dto;
    }
}
