package com.codingchallenge.minidoodlev1.api;

import com.codingchallenge.minidoodlev1.data.dto.MeetingCreationRequest;
import com.codingchallenge.minidoodlev1.data.dto.MeetingResponse;
import com.codingchallenge.minidoodlev1.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meetings")
@Tag(name = "Meetings", description = "Operations related to meeting scheduling")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @Operation(
            summary = "Create a new meeting",
            description = "Creates and schedules a meeting for the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Meeting created successfully",
                            content = @Content(schema = @Schema(implementation = MeetingResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Availability not found", content = @Content)
            }
    )
    public MeetingResponse createMeeting(
            @Parameter(description = "User ID performing the request", required = true)
            @RequestHeader Long userId,
            @RequestBody @Valid MeetingCreationRequest meetingCreationRequest) {
        return meetingService.createMeeting(userId, meetingCreationRequest);
    }
}
