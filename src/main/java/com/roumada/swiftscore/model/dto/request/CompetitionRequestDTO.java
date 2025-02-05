package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.validation.annotation.ValidCompetitionRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
@ValidCompetitionRequestDTO
public record CompetitionRequestDTO(
        @NotNull(message = "Name cannot be null")
        String name,
        CountryCode country,
        @Valid
        @NotNull(message = "Start date cannot be null")
        String startDate,
        @Valid
        @NotNull(message = "End date cannot be null")
        String endDate,
        List<Long> participantIds,
        int fillToParticipants,
        @NotNull(message = "Simulator values cannot be null")
        @Valid
        SimulationValues simulationValues
) {

    public List<Long> participantIds() {
        return participantIds == null ? Collections.emptyList() : participantIds;
    }

    public int participantsAmount() {
        int participantIdsSize = participantIds == null ? 0 : participantIds.size();
        return Math.max(participantIdsSize, fillToParticipants);
    }
}
