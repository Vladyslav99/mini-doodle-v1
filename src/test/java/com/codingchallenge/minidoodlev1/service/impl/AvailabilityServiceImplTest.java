package com.codingchallenge.minidoodlev1.service.impl;

import com.codingchallenge.minidoodlev1.data.dto.AvailabilityCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityResponse;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityUpdateRequest;
import com.codingchallenge.minidoodlev1.data.entity.Availability;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import com.codingchallenge.minidoodlev1.exception.AvailabilityConflictException;
import com.codingchallenge.minidoodlev1.exception.ResourceNotFoundException;
import com.codingchallenge.minidoodlev1.mapper.AvailabilityEntityMapper;
import com.codingchallenge.minidoodlev1.mapper.AvailabilityResponseMapper;
import com.codingchallenge.minidoodlev1.repository.AvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private AvailabilityEntityMapper availabilityEntityMapper;
    @Mock
    private AvailabilityResponseMapper availabilityResponseMapper;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private static final Long USER_ID = 1L;
    private static final Long AVAILABILITY_ID = 10L;
    private static final Instant START_TIME = Instant.parse("2025-12-01T10:00:00Z");
    private static final Instant END_TIME = Instant.parse("2025-12-01T11:00:00Z");

    private AvailabilityCreationRequest creationRequest;
    private Availability availabilityEntity;
    private AvailabilityResponse availabilityResponse;

    @BeforeEach
    void setUp() {
        creationRequest = new AvailabilityCreationRequest(START_TIME, END_TIME);

        availabilityEntity = new Availability();
        availabilityEntity.setId(AVAILABILITY_ID);
        availabilityEntity.setOwnerId(USER_ID);
        availabilityEntity.setStartDateTime(START_TIME);
        availabilityEntity.setEndDateTime(END_TIME);
        availabilityEntity.setAvailabilityStatus(AvailabilityStatus.FREE);

        availabilityResponse = new AvailabilityResponse(AVAILABILITY_ID, START_TIME, END_TIME, AvailabilityStatus.FREE);
    }

    @Test
    void createAvailability_Success() {
        when(availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(eq(USER_ID), eq(END_TIME), eq(START_TIME)))
                .thenReturn(false);
        when(availabilityEntityMapper.toEntity(creationRequest)).thenReturn(availabilityEntity);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availabilityEntity);
        when(availabilityResponseMapper.toResponse(availabilityEntity)).thenReturn(availabilityResponse);

        AvailabilityResponse actual = availabilityService.createAvailability(USER_ID, creationRequest);

        assertNotNull(actual);
        assertEquals(AVAILABILITY_ID, actual.id());
        assertEquals(START_TIME, actual.startDateTime());
    }

    @Test
    void createAvailability_Conflict() {
        when(availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(eq(USER_ID), eq(END_TIME), eq(START_TIME)))
                .thenReturn(true);

        assertThrows(AvailabilityConflictException.class, () ->
                availabilityService.createAvailability(USER_ID, creationRequest));
        verify(availabilityEntityMapper, never()).toEntity(any());
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void queryAvailabilities_Success() {
        Instant queryStart = START_TIME.minusSeconds(3600);
        Instant queryEnd = END_TIME.plusSeconds(3600);
        AvailabilityStatus status = AvailabilityStatus.FREE;
        List<Availability> entityList = List.of(availabilityEntity);
        when(availabilityRepository.findAll(any(Specification.class))).thenReturn(entityList);
        when(availabilityResponseMapper.toResponse(availabilityEntity)).thenReturn(availabilityResponse);

        List<AvailabilityResponse> result = availabilityService.queryAvailabilities(USER_ID, queryStart, queryEnd, status);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(AVAILABILITY_ID, result.get(0).id());
        verify(availabilityRepository).findAll(any(Specification.class));
        verify(availabilityResponseMapper, times(1)).toResponse(availabilityEntity);
    }

    @Test
    void deleteAvailability_Success() {
        when(availabilityRepository.existsByIdAndOwnerId(AVAILABILITY_ID, USER_ID)).thenReturn(true);

        availabilityService.deleteAvailability(USER_ID, AVAILABILITY_ID);

        verify(availabilityRepository).existsByIdAndOwnerId(AVAILABILITY_ID, USER_ID);
        verify(availabilityRepository).deleteById(AVAILABILITY_ID);
    }

    @Test
    void deleteAvailability_NotFound() {
        when(availabilityRepository.existsByIdAndOwnerId(AVAILABILITY_ID, USER_ID)).thenReturn(false);

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> availabilityService.deleteAvailability(USER_ID, AVAILABILITY_ID));

        assertTrue(actual.getMessage().contains(AVAILABILITY_ID.toString()));
        verify(availabilityRepository, never()).deleteById(any());
    }

    @Test
    void updateAvailability_Success() {
        Instant newStart = Instant.parse("2025-12-02T12:00:00Z");
        Instant newEnd = Instant.parse("2025-12-02T13:00:00Z");
        AvailabilityStatus newStatus = AvailabilityStatus.BUSY;
        AvailabilityUpdateRequest updateRequest = new AvailabilityUpdateRequest(newStart, newEnd, newStatus);
        Availability updatedEntity = Mockito.spy(availabilityEntity);
        updatedEntity.setStartDateTime(newStart);
        updatedEntity.setEndDateTime(newEnd);
        updatedEntity.setAvailabilityStatus(newStatus);
        AvailabilityResponse updatedResponse = new AvailabilityResponse(AVAILABILITY_ID, newStart, newEnd, AvailabilityStatus.FREE);
        when(availabilityRepository.findByIdAndOwnerId(AVAILABILITY_ID, USER_ID)).thenReturn(Optional.of(availabilityEntity));
        when(availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(eq(USER_ID), eq(newEnd), eq(newStart)))
                .thenReturn(false);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(updatedEntity);
        when(availabilityResponseMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

        AvailabilityResponse result = availabilityService.updateAvailability(USER_ID, AVAILABILITY_ID, updateRequest);

        assertNotNull(result);
        assertEquals(newStart, result.startDateTime());
        assertEquals(newEnd, result.endDateTime());
        assertEquals(newStart, availabilityEntity.getStartDateTime());
        assertEquals(newEnd, availabilityEntity.getEndDateTime());
        assertEquals(newStatus, availabilityEntity.getAvailabilityStatus());
        verify(availabilityRepository).findByIdAndOwnerId(AVAILABILITY_ID, USER_ID);
        verify(availabilityRepository).save(availabilityEntity);
    }

    @Test
    void updateAvailability_NotFound() {
        AvailabilityUpdateRequest updateRequest = new AvailabilityUpdateRequest(START_TIME, END_TIME, AvailabilityStatus.BUSY);
        when(availabilityRepository.findByIdAndOwnerId(AVAILABILITY_ID, USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class, () ->
                availabilityService.updateAvailability(USER_ID, AVAILABILITY_ID, updateRequest));

        assertTrue(actual.getMessage().contains(AVAILABILITY_ID.toString()));
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void updateAvailability_Conflict() {
        Instant conflictStart = Instant.parse("2025-12-02T12:00:00Z");
        Instant conflictEnd = Instant.parse("2025-12-02T13:00:00Z");
        AvailabilityUpdateRequest updateRequest = new AvailabilityUpdateRequest(conflictStart, conflictEnd, AvailabilityStatus.BUSY);
        when(availabilityRepository.findByIdAndOwnerId(AVAILABILITY_ID, USER_ID)).thenReturn(Optional.of(availabilityEntity));
        when(availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(eq(USER_ID), eq(conflictEnd), eq(conflictStart)))
                .thenReturn(true);

        assertThrows(AvailabilityConflictException.class, () ->
                availabilityService.updateAvailability(USER_ID, AVAILABILITY_ID, updateRequest));
        verify(availabilityRepository, never()).save(any());
    }
}
