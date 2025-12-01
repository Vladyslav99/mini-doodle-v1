package com.codingchallenge.minidoodlev1.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MeetingCreationRequest(

        @NotNull
        Long availabilityId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        List<String> participantEmails) {
}
