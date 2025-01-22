package com.roumada.swiftscore.validation.annotation;

import com.roumada.swiftscore.validation.validator.CompetitionRequestDTOValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CompetitionRequestDTOValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCompetitionRequestDTO {
    String message() default "Invalid object";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
