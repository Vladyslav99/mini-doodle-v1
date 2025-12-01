package com.codingchallenge.minidoodlev1.utils;

public interface ErrorMessages {

    String AVAILABILITY_NOT_FOUND_MESSAGE = "Availability not found by id = [%s]";
    String AVAILABILITY_CONFLICT_MESSAGE = "The requested time slot conflicts with an existing availability.";
    String AVAILABILITY_BOOKED_MESSAGE = "Availability with id = [%s] already booked by other user";

    String USER_NOT_FOUND_MESSAGE = "User not found by id = [%s]";
}
