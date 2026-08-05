package com.example.accommodation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.accommodation.dto.accommodation.AccommodationDto;
import com.example.accommodation.dto.accommodation.AccommodationRequestDto;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.mapper.AccommodationMapper;
import com.example.accommodation.model.Accommodation;
import com.example.accommodation.model.enums.AccommodationType;
import com.example.accommodation.repository.AccommodationRepository;
import com.example.accommodation.service.impl.AccommodationServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {
    private static final Long ID = 1L;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Mock
    private NotificationService notificationService;

    private AccommodationService accommodationService;


    @Test
    void create_validRequest_returnsDto() {
        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setType(AccommodationType.APARTMENT);
        requestDto.setLocation("Kyiv");
        requestDto.setSize("1 Bedroom");
        requestDto.setDailyRate(BigDecimal.valueOf(50));
        requestDto.setAvailability(3);

        Accommodation model = new Accommodation();
        model.setId(ID);
        model.setType(AccommodationType.APARTMENT);
        model.setLocation("Kyiv");

        AccommodationDto expectedDto = new AccommodationDto();
        expectedDto.setId(ID);

        when(accommodationMapper.toModel(requestDto)).thenReturn(model);
        when(accommodationRepository.save(model)).thenReturn(model);
        when(accommodationMapper.toDto(model)).thenReturn(expectedDto);
        doNothing().when(notificationService).sendNotification(any());

        AccommodationDto actual = accommodationService.create(requestDto);

        assertThat(actual.getId()).isEqualTo(ID);
        verify(notificationService).sendNotification(any());
    }

    @Test
    void findById_notFound_throwsException() {
        when(accommodationRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.findById(ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(ID));
    }
}
