package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.validation.annotation.ValidCreateCompetitionRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class CreateCompetitionRequestValidator implements ConstraintValidator<ValidCreateCompetitionRequest, CreateCompetitionRequest> {

    @Value("${application.maximum_competition_duration}")
    private int MAXIMUM_COMPETITION_DURATION;

    @Override
    public boolean isValid(CreateCompetitionRequest request, ConstraintValidatorContext context) {
        if (request.competitionParameters() == null) {
            return false;
        }

        if (request.participantsAmount() == 0) {
            context
                    .buildConstraintViolationWithTemplate("Neither participants nor footballClubIDs have been set")
                    .addConstraintViolation();
            return false;
        }

        if (request.participantsAmount() - 1 <= request.competitionParameters().relegationSpots()) {
            context
                    .buildConstraintViolationWithTemplate("Amount of participants must be at least greater than two than relegation spots")
                    .addConstraintViolation();
            return false;
        }

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

        if (ChronoUnit.DAYS.between(start, end) + 1 < request.participantsAmount() * 2L - 2) {
            context
                    .buildConstraintViolationWithTemplate("Competition needs at least %s days for a competition with %s clubs."
                            .formatted(request.participantsAmount() * 2L - 2, request.participantsAmount()))
                    .addConstraintViolation();
            return false;
        }
        if (ChronoUnit.DAYS.between(start, end) > MAXIMUM_COMPETITION_DURATION) {
            context
                    .buildConstraintViolationWithTemplate("The amount of days for a competition has exceed maximum duration [%s]".formatted(MAXIMUM_COMPETITION_DURATION))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
