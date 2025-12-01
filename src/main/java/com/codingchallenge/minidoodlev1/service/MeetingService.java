package com.codingchallenge.minidoodlev1.service;

import com.codingchallenge.minidoodlev1.data.dto.MeetingCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.MeetingResponse;
import com.codingchallenge.minidoodlev1.data.entity.Availability;
import com.codingchallenge.minidoodlev1.data.entity.Meeting;
import com.codingchallenge.minidoodlev1.data.entity.User;
import com.codingchallenge.minidoodlev1.exception.AvailabilityBookedException;
import com.codingchallenge.minidoodlev1.exception.ResourceNotFoundException;
import jakarta.persistence.OptimisticLockException;

public interface MeetingService {

    /**
     * Creates a new meeting by consuming a previously marked 'FREE' availability slot and
     * assigning participants.
     *
     * <p>This method performs the following critical steps within a single transaction:</p>
     * <ol>
     * <li>Validates the existence of the {@code organizerId} and fetches the {@link User} entity.</li>
     * <li>Verifies that the specified {@code availabilityId} exists
     * and is currently marked as {@code FREE}.</li>
     * <li>Updates the found {@link Availability} slot's status to {@code BUSY} to reserve it.</li>
     * <li>Fetches all valid {@link User} entities corresponding to the participant emails
     * and adds the organizer to the list of participants.</li>
     * <li>Saves the new {@link Meeting} record to the database.</li>
     * <li>Saves the updated {@link Availability} status. This step uses optimistic locking
     * to prevent concurrent double-booking of the same slot.</li>
     * </ol>
     *
     * @param organizerId The unique identifier of the user creating and owning the meeting.
     * @param meetingCreationRequest The DTO containing the meeting title, description, the ID of the
     * availability slot to consume, and the list of participant emails.
     * @return A {@link MeetingResponse} DTO representing the newly created and saved meeting.
     * @throws ResourceNotFoundException if the specified {@code organizerId} does not exist,
     * or if the {@code availabilityId} does not exist, is not owned by the organizer,
     * or is already marked as {@code BUSY}.
     * @throws AvailabilityBookedException if the {@link Availability} slot was successfully fetched
     * and updated in the current transaction, but the final save fails due to an
     * {@link OptimisticLockException}, indicating the slot was concurrently modified
     * and booked by another user (race condition).
     */
    MeetingResponse createMeeting(Long organizerId, MeetingCreationRequest meetingCreationRequest);
}
