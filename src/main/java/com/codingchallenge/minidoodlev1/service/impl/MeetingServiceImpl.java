package com.codingchallenge.minidoodlev1.service.impl;

import com.codingchallenge.minidoodlev1.data.dto.MeetingCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.MeetingResponse;
import com.codingchallenge.minidoodlev1.data.entity.Availability;
import com.codingchallenge.minidoodlev1.data.entity.Meeting;
import com.codingchallenge.minidoodlev1.data.entity.User;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import com.codingchallenge.minidoodlev1.exception.AvailabilityBookedException;
import com.codingchallenge.minidoodlev1.exception.ResourceNotFoundException;
import com.codingchallenge.minidoodlev1.mapper.MeetingResponseMapper;
import com.codingchallenge.minidoodlev1.repository.AvailabilityRepository;
import com.codingchallenge.minidoodlev1.repository.MeetingRepository;
import com.codingchallenge.minidoodlev1.repository.UserRepository;
import com.codingchallenge.minidoodlev1.service.MeetingService;
import com.codingchallenge.minidoodlev1.specification.AvailabilitySpecification;
import com.codingchallenge.minidoodlev1.utils.ErrorMessages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final MeetingResponseMapper meetingResponseMapper;

    @Override
    @Transactional
    public MeetingResponse createMeeting(Long organizerId, MeetingCreationRequest meetingCreationRequest) {
        Long availabilityId = meetingCreationRequest.availabilityId();
        Availability availability = availabilityRepository.findOne(
                        Specification.where(AvailabilitySpecification.availabilityId(availabilityId))
                                .and(AvailabilitySpecification.availabilityStatus(AvailabilityStatus.FREE)))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.AVAILABILITY_NOT_FOUND_MESSAGE, availabilityId)));
        availability.setAvailabilityStatus(AvailabilityStatus.BUSY);

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_MESSAGE, organizerId)));
        List<User> participants = userRepository.findAllByEmailIn(meetingCreationRequest.participantEmails());
        participants.add(organizer);

        Meeting newMeeting = Meeting.builder()
                .title(meetingCreationRequest.title())
                .description(meetingCreationRequest.description())
                .organizer(organizer)
                .participants(participants)
                .build();
        Meeting savedMeeting = meetingRepository.save(newMeeting);
        try {
            availabilityRepository.save(availability);
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new AvailabilityBookedException(String.format(ErrorMessages.AVAILABILITY_BOOKED_MESSAGE, availabilityId));
        }
        return meetingResponseMapper.toResponse(savedMeeting);
    }
}
