package com.codingchallenge.minidoodlev1.api;

import com.codingchallenge.minidoodlev1.data.dto.AvailabilityCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityResponse;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityUpdateRequest;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import com.codingchallenge.minidoodlev1.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availabilities")
@Tag(name = "Availabilities", description = "Operations related to user availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @Operation(
            summary = "Create a new availability",
            description = "Creates a new availability record for the given user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Availability created successfully",
                            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
            }
    )
    public AvailabilityResponse createAvailability(
            @Parameter(description = "User ID performing the request", required = true)
            @RequestHeader Long userId, @RequestBody @Valid AvailabilityCreationRequest availabilityCreationRequest) {
        return availabilityService.createAvailability(userId, availabilityCreationRequest);
    }

    @GetMapping
    @Operation(
            summary = "Query availabilities",
            description = "Search availabilities by optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Availabilities retrieved successfully",
                            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))),
            }
    )
    public List<AvailabilityResponse> queryAvailabilities(
            @Parameter(description = "User ID performing the request", required = true)
            @RequestHeader Long userId,
            @Parameter(description = "Availability owner ID")
            @RequestParam(required = false) Long ownerId,
            @Parameter(description = "Start date-time filter (ISO-8601)")
            @RequestParam(required = false) Instant startDateTime,
            @Parameter(description = "End date-time filter (ISO-8601)")
            @RequestParam(required = false) Instant endDateTime,
            @Parameter(description = "Availability status filter")
            @RequestParam(required = false) AvailabilityStatus availabilityStatus
    ) {
        return availabilityService.queryAvailabilities(Objects.isNull(ownerId) ? userId : ownerId, startDateTime, endDateTime, availabilityStatus);
    }

    @PutMapping("/{availabilityId}")
    @Operation(
            summary = "Update an availability",
            description = "Modify an existing availability record",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Availability updated successfully",
                            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Availability not found", content = @Content)
            }
    )
    public AvailabilityResponse updateAvailability(
            @Parameter(description = "User ID performing the request", required = true)
            @RequestHeader Long userId,
            @Parameter(description = "ID of availability to update", required = true)
            @PathVariable Long availabilityId,
            @RequestBody @Valid AvailabilityUpdateRequest availabilityUpdateRequest
    ) {
        return availabilityService.updateAvailability(userId, availabilityId, availabilityUpdateRequest);
    }

    @DeleteMapping("/{availabilityId}")
    @Operation(
            summary = "Delete an availability",
            description = "Deletes the specified availability",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Availability not found", content = @Content)
            }
    )
    public void deleteAvailability(
            @Parameter(description = "User ID performing the request", required = true)
            @RequestHeader Long userId,
            @Parameter(description = "ID of availability to delete", required = true)
            @PathVariable Long availabilityId
    ) {
        availabilityService.deleteAvailability(userId, availabilityId);
    }
}
