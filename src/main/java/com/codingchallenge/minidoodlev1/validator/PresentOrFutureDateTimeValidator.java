package com.codingchallenge.minidoodlev1.validator;

import com.codingchallenge.minidoodlev1.annotation.PresentOrFutureDateTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.util.Objects;

public class PresentOrFutureDateTimeValidator implements ConstraintValidator<PresentOrFutureDateTime, Instant> {

    @Override
    public boolean isValid(Instant instant, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(instant)) {
            return true;
        }
        return !instant.isBefore(Instant.now());
    }
}
