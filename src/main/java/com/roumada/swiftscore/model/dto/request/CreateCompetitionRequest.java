package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import com.roumada.swiftscore.validation.annotation.ValidCompetitionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
@ValidCompetitionRequest
public record CreateCompetitionRequest(
        @NotNull(message = "Name cannot be null")
        String name,
        @NotNull(message = "Country code cannot be null")
        CountryCode country,
        @NotNull(message = "Start date cannot be null")
        String startDate,
        @NotNull(message = "End date cannot be null")
        String endDate,
        @NotNull(message = "Competition competitionParameters cannot be null")
        CompetitionParameters competitionParameters,
        @Valid
        @NotNull(message = "Simulation competitionParameters cannot be null")
        SimulationParameters simulationParameters
) {
    public Integer participants() {
        return ObjectUtils.defaultIfNull(competitionParameters.participants(), 0);
    }

    public List<Long> participantIds() {
        return ObjectUtils.defaultIfNull(competitionParameters.participantIds(), Collections.emptyList());
    }

    public int participantsAmount() {
        int participantIdsSize = competitionParameters.participantIds() == null ? 0 : competitionParameters.participantIds().size();
        return Math.max(participantIdsSize, participants());
    }
}
