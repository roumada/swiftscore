package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.validation.annotation.ValidLocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionRequestDTO(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Competition type cannot be null")
        Competition.CompetitionType type,
        CountryCode country,
        @NotNull(message = "Start date cannot be null")
        @ValidLocalDate(message = "Please provide a valid date in the format YYYY-MM-DD")
        String startDate,
        @NotNull(message = "End date cannot be null")
        @ValidLocalDate(message = "Please provide a valid date in the format YYYY-MM-DD")
        String endDate,
        @NotNull(message = "Participant IDs list cannot be null")
        List<Long> participantIds,
        @NotNull(message = "Simulator values cannot be null")
        @Valid
        SimulationValues simulationValues
) {
}
