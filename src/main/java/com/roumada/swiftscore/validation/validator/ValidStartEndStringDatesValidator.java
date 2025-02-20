package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.StartEndStringDates;
import com.roumada.swiftscore.validation.annotation.ValidStartEndStringDates;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidStartEndStringDatesValidator implements ConstraintValidator<ValidStartEndStringDates, StartEndStringDates> {

    @Override
    public boolean isValid(StartEndStringDates dateHolder, ConstraintValidatorContext context) {
        String startDate = dateHolder.getStartDate();
        String endDate = dateHolder.getEndDate();
        boolean isDateMissing = false;
        if (StringUtils.isEmpty(startDate)) {
            context
                    .buildConstraintViolationWithTemplate("Start date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (StringUtils.isEmpty(endDate)) {
            context
                    .buildConstraintViolationWithTemplate("End date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (isDateMissing) return false;

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
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
