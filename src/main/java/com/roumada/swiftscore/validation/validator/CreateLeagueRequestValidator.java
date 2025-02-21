package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.request.CreateLeagueCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.validation.annotation.ValidCreateLeagueRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CreateLeagueRequestValidator implements ConstraintValidator<ValidCreateLeagueRequest, CreateLeagueRequest> {

    @Value("${application.maximum_competition_duration}")
    private int MAXIMUM_COMPETITION_DURATION;

    @Override
    public boolean isValid(CreateLeagueRequest request, ConstraintValidatorContext context) {
        if (!StartEndStringDatesValidator.isValid(request, context)) return false;

        var seasonDuration = ChronoUnit.DAYS.between(LocalDate.parse(request.startDate()), LocalDate.parse(request.endDate()));
        for (CreateLeagueCompetitionRequest competitionRequest : request.competitions()) {
            if (seasonDuration + 1 < competitionRequest.participantsAmount() * 2L - 2) {
                context
                        .buildConstraintViolationWithTemplate("Competition needs at least %s days for a competition with %s clubs."
                                .formatted(competitionRequest.participantsAmount() * 2L - 2, competitionRequest.participantsAmount()))
                        .addConstraintViolation();
                return false;
            }
            if (seasonDuration > MAXIMUM_COMPETITION_DURATION) {
                context
                        .buildConstraintViolationWithTemplate("The amount of days for a competition has exceed maximum duration [%s]".formatted(MAXIMUM_COMPETITION_DURATION))
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
