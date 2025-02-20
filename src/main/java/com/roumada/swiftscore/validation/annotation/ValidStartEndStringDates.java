package com.roumada.swiftscore.validation.annotation;

import com.roumada.swiftscore.validation.validator.ValidStartEndStringDatesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidStartEndStringDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartEndStringDates {
    String message() default "Invalid object";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
