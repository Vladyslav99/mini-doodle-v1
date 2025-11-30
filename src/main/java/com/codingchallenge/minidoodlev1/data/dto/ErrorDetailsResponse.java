package com.codingchallenge.minidoodlev1.data.dto;

import java.time.Instant;
import java.util.List;

public record ErrorDetailsResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ValidationError> validationErrors) {
}
