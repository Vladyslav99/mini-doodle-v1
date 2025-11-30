package com.codingchallenge.minidoodlev1.data.dto;

import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;

import java.time.Instant;

public record AvailabilityResponse(Long id, Instant startDateTime, Instant endDateTime, AvailabilityStatus availabilityStatus) {
}
