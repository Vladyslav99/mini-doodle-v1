package com.codingchallenge.minidoodlev1.annotation;

import com.codingchallenge.minidoodlev1.validator.PresentOrFutureDateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PresentOrFutureDateTimeValidator.class)
@Documented
public @interface PresentOrFutureDateTime {

    String message() default "StartDateTime must be present or in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
