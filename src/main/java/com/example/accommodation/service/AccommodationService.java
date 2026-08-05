package com.example.accommodation.service;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import java.util.List;

public interface AccommodationService {

    AccommodationDto create(AccommodationRequestDto requestDto);

    List<AccommodationDto> findAll();

    AccommodationDto findById(Long id);

    AccommodationDto update(Long id, AccommodationRequestDto requestDto);

    void delete(Long id);
}
