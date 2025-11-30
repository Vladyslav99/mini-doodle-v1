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
import com.codingchallenge.minidoodlev1.service.AvailabilityService;
import com.codingchallenge.minidoodlev1.specification.AvailabilitySpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private static final String AVAILABILITY_NOT_FOUND_MESSAGE = "Availability not found by id = [%s]";
    private static final String AVAILABILITY_CONFLICT_MESSAGE = "The requested time slot conflicts with an existing availability.";

    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityEntityMapper availabilityEntityMapper;
    private final AvailabilityResponseMapper availabilityResponseMapper;

    @Override
    @Transactional
    public AvailabilityResponse createAvailability(Long userId, AvailabilityCreationRequest availabilityCreationRequest) {
        Instant startDateTime = availabilityCreationRequest.startDateTime();
        Instant endDateTime = availabilityCreationRequest.endDateTime();
        if (availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(userId, endDateTime, startDateTime)) {
            throw new AvailabilityConflictException(AVAILABILITY_CONFLICT_MESSAGE);
        }
        Availability newAvailability = availabilityEntityMapper.toEntity(availabilityCreationRequest);
        newAvailability.setOwnerId(userId);
        newAvailability.setAvailabilityStatus(AvailabilityStatus.FREE);
        return availabilityResponseMapper.toResponse(availabilityRepository.save(newAvailability));
    }

    @Override
    public List<AvailabilityResponse> queryAvailabilities(Long ownerId, Instant startDateTime, Instant endDateTime, AvailabilityStatus availabilityStatus) {
        return availabilityRepository.findAll(
                        Specification.where(AvailabilitySpecification.ownerId(ownerId))
                                .and(AvailabilitySpecification.availabilityStatus(availabilityStatus))
                                .and(AvailabilitySpecification.overlapping(startDateTime, endDateTime))
                                .and(AvailabilitySpecification.orderByStartDateTimeAsc)).stream()
                .map(availabilityResponseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAvailability(Long ownerId, Long availabilityId) {
        if (availabilityRepository.existsByIdAndOwnerId(availabilityId, ownerId)) {
            availabilityRepository.deleteById(availabilityId);
            return;
        }
        throw new ResourceNotFoundException(String.format(AVAILABILITY_NOT_FOUND_MESSAGE, availabilityId));
    }

    @Override
    @Transactional
    public AvailabilityResponse updateAvailability(Long ownerId, Long availabilityId, AvailabilityUpdateRequest availabilityUpdateRequest) {
        Availability availability = availabilityRepository.findByIdAndOwnerId(availabilityId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AVAILABILITY_NOT_FOUND_MESSAGE, availabilityId)));
        Instant startDateTime = availabilityUpdateRequest.startDateTime();
        Instant endDateTime = availabilityUpdateRequest.endDateTime();
        if (availabilityRepository.existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(ownerId, endDateTime, startDateTime)) {
            throw new AvailabilityConflictException(AVAILABILITY_CONFLICT_MESSAGE);
        }
        availability.setStartDateTime(startDateTime);
        availability.setEndDateTime(endDateTime);
        availability.setAvailabilityStatus(availabilityUpdateRequest.availabilityStatus());
        return availabilityResponseMapper.toResponse(availabilityRepository.save(availability));
    }

}
