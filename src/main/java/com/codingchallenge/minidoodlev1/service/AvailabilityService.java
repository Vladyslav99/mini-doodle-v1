package com.codingchallenge.minidoodlev1.service;

import com.codingchallenge.minidoodlev1.data.dto.AvailabilityCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityResponse;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityUpdateRequest;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import com.codingchallenge.minidoodlev1.exception.AvailabilityConflictException;
import com.codingchallenge.minidoodlev1.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;

public interface AvailabilityService {
    /**
     * Creates a new availability entry for a specific user based on the provided time slot.
     * * <p>This method checks for time slot conflicts against any existing availabilities
     * for the user, preventing overlaps.</p>
     *
     * @param userId                      The unique identifier (ID) of the user for whom the availability is being created.
     * @param availabilityCreationRequest The DTO containing the desired start and end date times
     *                                    for the new availability slot. This request object is
     *                                    expected to pass all necessary Spring validation checks.
     * @return An {@code AvailabilityResponse} DTO representing the newly created and persisted
     * availability resource, including its generated ID and auditing information.
     * @throws AvailabilityConflictException if the requested time slot in {@code availabilityCreationRequest}
     *                                       overlaps or conflicts with an availability slot that already
     *                                       exists for the specified user.
     */
    AvailabilityResponse createAvailability(Long userId, AvailabilityCreationRequest availabilityCreationRequest);

    /**
     * Queries and retrieves a list of availability slots that match the specified criteria.
     *
     * <p>This method finds all existing availability records associated with the given owner ID
     * that fall within the specified time range and have the designated availability status.
     * The time range query uses overlap logic (existing slot intersects with the query window)
     * rather than strict containment.</p>
     *
     * @param ownerId            The unique identifier of the user (owner) whose availabilities are being queried.
     * @param startDateTime      The inclusive start time of the query window (the lower bound for the time range check).
     * @param endDateTime        The inclusive end time of the query window (the upper bound for the time range check).
     * @param availabilityStatus The status of the availability slots to filter by (e.g., FREE, BUSY).
     * @return A {@code List} of {@code AvailabilityResponse} DTOs representing the slots that match
     * all filtering parameters. Returns an empty list if no matching slots are found.
     */
    List<AvailabilityResponse> queryAvailabilities(Long ownerId, Instant startDateTime, Instant endDateTime, AvailabilityStatus availabilityStatus);

    /**
     * Deletes a specific availability entry identified by its ID, provided the request
     * originates from the resource's owner.
     *
     * <p>This method enforces an ownership check to ensure that only the user identified
     * by {@code ownerId} can delete the availability specified by {@code availabilityId}.
     * It first verifies the existence and ownership of the resource before proceeding
     * with the hard deletion from the database. Successful execution renders the
     * resource permanently unavailable.</p>
     *
     * @param availabilityId The unique identifier (ID) of the availability entry to be deleted.
     * @param ownerId The unique identifier (ID) of the user attempting the deletion. This ID is
     * used to verify ownership of the availability resource before the deletion is permitted.
     * @throws ResourceNotFoundException if no availability entry exists with the given
     * {@code availabilityId}, or if an entry exists but the {@code ownerId} does not
     * match the resource's owner.
     */
    void deleteAvailability(Long availabilityId, Long ownerId);

    /**
     * Updates an existing availability entry with new time and status details.
     *
     * <p>This method performs a partial or full update of an existing availability slot.
     * It strictly validates that the availability belongs to the specified owner
     * and checks for any new time conflicts before persisting changes. It also
     * ensures that the ID in the request path matches the ID of the resource
     * being updated.</p>
     *
     * @param ownerId The unique identifier of the user (owner) attempting the update.
     * @param availabilityId The unique identifier (ID) of the availability resource to be updated.
     * @param availabilityUpdateRequest The DTO containing the proposed changes, which may include
     * new start time, end time, and/or status.
     * @return An {@code AvailabilityResponse} DTO representing the updated and saved resource.
     * @throws ResourceNotFoundException if the availability entry specified by {@code availabilityId}
     * does not exist, or if the specified {@code ownerId} does not
     * own the resource.
     * @throws AvailabilityConflictException if the updated time slot conflicts or overlaps with
     * any other existing availability slots for the same owner.
     */
    AvailabilityResponse updateAvailability(Long ownerId, Long availabilityId, AvailabilityUpdateRequest availabilityUpdateRequest);
}
