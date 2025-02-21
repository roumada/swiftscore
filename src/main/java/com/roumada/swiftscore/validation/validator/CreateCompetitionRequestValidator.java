package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.validation.annotation.ValidCreateCompetitionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CreateCompetitionRequestValidator implements ConstraintValidator<ValidCreateCompetitionRequest, CreateCompetitionRequest> {

    @Value("${application.maximum_competition_duration}")
    private int MAXIMUM_COMPETITION_DURATION;

    @Override
    public boolean isValid(CreateCompetitionRequest request, ConstraintValidatorContext context) {
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

        if (!StartEndStringDatesValidator.isValid(request, context)) return false;

        LocalDate start = LocalDate.parse(request.startDate());
        LocalDate end = LocalDate.parse(request.endDate());

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
