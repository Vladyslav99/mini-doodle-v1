package com.codingchallenge.minidoodlev1.data.dto;

import java.util.List;


public record MeetingResponse(Long id, String title, String description, UserResponse organizer, List<UserResponse> participants) {
}
