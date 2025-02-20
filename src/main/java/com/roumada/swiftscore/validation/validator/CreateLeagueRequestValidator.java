package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.validation.annotation.ValidCreateLeagueRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateLeagueRequestValidator implements ConstraintValidator<ValidCreateLeagueRequest, CreateLeagueRequest> {

    @Override
    public boolean isValid(CreateLeagueRequest request, ConstraintValidatorContext context) {
        boolean isDateMissing = false;
        if (StringUtils.isEmpty(request.startDate())) {
            context
                    .buildConstraintViolationWithTemplate("Start date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (StringUtils.isEmpty(request.endDate())) {
            context
                    .buildConstraintViolationWithTemplate("End date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (isDateMissing) return false;

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(request.startDate());
            end = LocalDate.parse(request.endDate());
        } catch (DateTimeParseException e) {
            context
                    .buildConstraintViolationWithTemplate(
                            "Unparsable data format for one of the dates (must be YYYY-MM-DD)")
                    .addConstraintViolation();
            return false;
        }
        if (start.isAfter(end)) {
            context
                    .buildConstraintViolationWithTemplate("Start date cannot be ahead of end date")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
