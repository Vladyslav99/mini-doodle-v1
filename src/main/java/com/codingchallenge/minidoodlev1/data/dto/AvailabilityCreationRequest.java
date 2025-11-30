package com.codingchallenge.minidoodlev1.data.dto;

import com.codingchallenge.minidoodlev1.annotation.PresentOrFutureDateTime;
import com.codingchallenge.minidoodlev1.annotation.ValidAvailabilityPeriod;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@ValidAvailabilityPeriod
public record AvailabilityCreationRequest(

        @NotNull
        @PresentOrFutureDateTime
        Instant startDateTime,

        @NotNull
        Instant endDateTime) {
}
