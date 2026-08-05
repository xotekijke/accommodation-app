package com.example.accommodation.service.impl;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.mapper.AccommodationMapper;
import com.example.accommodation.model.Accommodation;
import com.example.accommodation.repository.AccommodationRepository;
import com.example.accommodation.service.AccommodationService;
import com.example.accommodation.service.NotificationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final NotificationService notificationService;

    public AccommodationServiceImpl(AccommodationRepository accommodationRepository,
            AccommodationMapper accommodationMapper,
            NotificationService notificationService) {
        this.accommodationRepository = accommodationRepository;
        this.accommodationMapper = accommodationMapper;
        this.notificationService = notificationService;
    }

    @Override
    public AccommodationDto create(AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.save(
                accommodationMapper.toModel(requestDto));
        notificationService.sendNotification(
                "New accommodation created: " + accommodation.getType()
                        + " at " + accommodation.getLocation()
                        + " (id " + accommodation.getId() + ")");
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public List<AccommodationDto> findAll() {
        return accommodationRepository.findAll().stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto findById(Long id) {
        return accommodationMapper.toDto(getAccommodationOrThrow(id));
    }

    @Override
    @Transactional
    public AccommodationDto update(Long id, AccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        accommodationMapper.updateModel(accommodation, requestDto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public void delete(Long id) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        accommodationRepository.deleteById(id);
        notificationService.sendNotification(
                "Accommodation released: " + accommodation.getType()
                        + " at " + accommodation.getLocation()
                        + " (id " + accommodation.getId() + ")");
    }

    private Accommodation getAccommodationOrThrow(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find accommodation by id " + id));
    }
}
