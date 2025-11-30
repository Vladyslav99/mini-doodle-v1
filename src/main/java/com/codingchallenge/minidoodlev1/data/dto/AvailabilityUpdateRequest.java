package com.codingchallenge.minidoodlev1.data.dto;

import com.codingchallenge.minidoodlev1.annotation.PresentOrFutureDateTime;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AvailabilityUpdateRequest(

        @NotNull
        @PresentOrFutureDateTime
        Instant startDateTime,

        @NotNull
        Instant endDateTime,

        @NotNull
        AvailabilityStatus availabilityStatus
) {
}
