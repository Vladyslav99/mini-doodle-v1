package com.codingchallenge.minidoodlev1.service.impl;

import com.codingchallenge.minidoodlev1.data.dto.MeetingCreationRequest;
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
import com.codingchallenge.minidoodlev1.utils.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    private static final String ORGANIZER_EMAIL = "organizer@example.com";
    private static final String ORGANIZER_FIRST_NAME = "Alex";
    private static final String ORGANIZER_LAST_NAME = "Alexov";
    private static final Long ORGANIZER_ID = 10L;

    private static final String PARTICIPANT_EMAIL = "p@example.com";
    private static final String PARTICIPANT_FIRST_NAME = "Bob";
    private static final String PARTICIPANT_LAST_NAME = "Bobov";
    private static final Long PARTICIPANT_ID = 20L;

    private static final String MEETING_TITLE = "Meeting important title";
    private static final String MEETING_DESCRIPTION = "Meeting important description";
    private static final Long MEETING_ID = 500L;

    private static final Long AVAILABILITY_ID = 100L;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingResponseMapper meetingResponseMapper;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Test
    void createMeeting_Success_ShouldSaveAndReturnResponse() {
        Availability freeAvailability = createFreeAvailability();
        when(availabilityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(freeAvailability));
        User organizer = createOrganizer();
        when(userRepository.findById(ORGANIZER_ID)).thenReturn(Optional.of(organizer));
        User participant = createParticipant();
        when(userRepository.findAllByEmailIn(anyList())).thenReturn(new ArrayList<>(List.of(participant)));
        Meeting savedMeeting = createSavedMeeting();
        when(meetingRepository.save(any(Meeting.class))).thenReturn(savedMeeting);
        MeetingCreationRequest meetingCreationRequest = createMeetingCreationRequest();

        meetingService.createMeeting(ORGANIZER_ID, meetingCreationRequest);

        assertEquals(AvailabilityStatus.BUSY, freeAvailability.getAvailabilityStatus());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(availabilityRepository, times(1)).save(freeAvailability);
        verify(meetingResponseMapper, times(1)).toResponse(savedMeeting);
    }

    @Test
    void createMeeting_WhenAvailabilityNotFoundOrNotFree_ShouldThrowResourceNotFoundException() {
        when(availabilityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        MeetingCreationRequest meetingCreationRequest = createMeetingCreationRequest();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            meetingService.createMeeting(ORGANIZER_ID, meetingCreationRequest);
        });

        assertEquals(String.format(ErrorMessages.AVAILABILITY_NOT_FOUND_MESSAGE, AVAILABILITY_ID), exception.getMessage());
        verify(meetingRepository, never()).save(any(Meeting.class));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void createMeeting_WhenOrganizerNotFound_ShouldThrowResourceNotFoundException() {
        Availability freeAvailability = createFreeAvailability();
        when(availabilityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(freeAvailability));
        when(userRepository.findById(ORGANIZER_ID)).thenReturn(Optional.empty());
        MeetingCreationRequest meetingCreationRequest = createMeetingCreationRequest();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            meetingService.createMeeting(ORGANIZER_ID, meetingCreationRequest);
        });

        assertEquals(String.format(ErrorMessages.USER_NOT_FOUND_MESSAGE, ORGANIZER_ID), exception.getMessage());
        verify(meetingRepository, never()).save(any(Meeting.class));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void createMeeting_WhenOptimisticLockingFails_ShouldThrowAvailabilityBookedException() {
        Availability freeAvailability = createFreeAvailability();
        when(availabilityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(freeAvailability));
        User organizer = createOrganizer();
        when(userRepository.findById(ORGANIZER_ID)).thenReturn(Optional.of(organizer));
        User participant = createParticipant();
        when(userRepository.findAllByEmailIn(anyList())).thenReturn(new ArrayList<>(List.of(participant)));
        Meeting savedMeeting = createSavedMeeting();
        when(meetingRepository.save(any(Meeting.class))).thenReturn(savedMeeting);
        MeetingCreationRequest meetingCreationRequest = createMeetingCreationRequest();
        doThrow(ObjectOptimisticLockingFailureException.class)
                .when(availabilityRepository).save(any(Availability.class));

        AvailabilityBookedException exception = assertThrows(AvailabilityBookedException.class, () -> {
            meetingService.createMeeting(ORGANIZER_ID, meetingCreationRequest);
        });

        assertEquals(String.format(ErrorMessages.AVAILABILITY_BOOKED_MESSAGE, AVAILABILITY_ID), exception.getMessage());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(availabilityRepository, times(1)).save(any(Availability.class));
        verifyNoInteractions(meetingResponseMapper);
    }

    private User createOrganizer() {
        return User.builder()
                .id(ORGANIZER_ID)
                .firstName(ORGANIZER_FIRST_NAME)
                .lastName(ORGANIZER_LAST_NAME)
                .email(ORGANIZER_EMAIL)
                .build();
    }

    private User createParticipant() {
        return User.builder()
                .id(PARTICIPANT_ID)
                .firstName(PARTICIPANT_FIRST_NAME)
                .lastName(PARTICIPANT_LAST_NAME)
                .email(PARTICIPANT_EMAIL)
                .build();
    }

    private Availability createFreeAvailability() {
        return Availability.builder()
                .id(AVAILABILITY_ID)
                .ownerId(ORGANIZER_ID)
                .availabilityStatus(AvailabilityStatus.FREE)
                .build();
    }

    private MeetingCreationRequest createMeetingCreationRequest() {
        return new MeetingCreationRequest(AVAILABILITY_ID,
                MEETING_TITLE,
                MEETING_DESCRIPTION,
                List.of(PARTICIPANT_EMAIL)
        );
    }

    private Meeting createSavedMeeting() {
        return Meeting.builder()
                .id(MEETING_ID)
                .title(MEETING_TITLE)
                .description(MEETING_DESCRIPTION)
                .organizer(createOrganizer())
                .participants(List.of(createParticipant()))
                .build();
    }
}
