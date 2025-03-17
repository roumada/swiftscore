package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CreateLeagueCompetitionRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,
        @NotNull(message = "Competition parameters cannot be null")
        CompetitionParameters competitionParameters,
        @Valid
        @NotNull(message = "Simulation parameters cannot be null")
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
