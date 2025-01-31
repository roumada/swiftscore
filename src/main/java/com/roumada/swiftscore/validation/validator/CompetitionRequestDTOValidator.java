package com.roumada.swiftscore.validation.validator;

import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.validation.annotation.ValidCompetitionRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class CompetitionRequestDTOValidator implements ConstraintValidator<ValidCompetitionRequestDTO, CompetitionRequestDTO> {

    @Value("${application.maximum_competition_duration}")
    private int MAXIMUM_COMPETITION_DURATION;

    @Override
    public boolean isValid(CompetitionRequestDTO dto, ConstraintValidatorContext context) {
        boolean isDateMissing = false;
        if (dto.startDate() == null || dto.startDate().isEmpty()) {
            context
                    .buildConstraintViolationWithTemplate("Start date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (dto.endDate() == null || dto.endDate().isEmpty()) {
            context
                    .buildConstraintViolationWithTemplate("End date must be present")
                    .addConstraintViolation();
            isDateMissing = true;
        }
        if (isDateMissing) return false;

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(dto.startDate());
            end = LocalDate.parse(dto.endDate());
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
        if (ChronoUnit.DAYS.between(start, end) + 1 < dto.participantIds().size() * 2L - 2) {
            context
                    .buildConstraintViolationWithTemplate("Competition needs at least %s days for a competition with %s clubs."
                            .formatted(dto.participantIds().size() * 2L - 2, dto.participantIds().size()))
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
