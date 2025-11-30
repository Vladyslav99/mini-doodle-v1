package com.codingchallenge.minidoodlev1.validator;

import com.codingchallenge.minidoodlev1.annotation.ValidAvailabilityPeriod;
import com.codingchallenge.minidoodlev1.data.dto.AvailabilityCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.util.Objects;

public class AvailabilityPeriodValidator implements ConstraintValidator<ValidAvailabilityPeriod, AvailabilityCreationRequest> {

    @Override
    public boolean isValid(AvailabilityCreationRequest availabilityCreationRequest, ConstraintValidatorContext constraintValidatorContext) {
        Instant startDateTime = availabilityCreationRequest.startDateTime();
        Instant endDateTime = availabilityCreationRequest.endDateTime();
        if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            return true;
        }
        return endDateTime.isAfter(startDateTime);
    }
}
