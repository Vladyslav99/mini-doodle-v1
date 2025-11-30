package com.codingchallenge.minidoodlev1.annotation;

import com.codingchallenge.minidoodlev1.validator.AvailabilityPeriodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AvailabilityPeriodValidator.class)
@Documented
public @interface ValidAvailabilityPeriod {

    String message() default "EndDateTime must be after startDateTime.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
