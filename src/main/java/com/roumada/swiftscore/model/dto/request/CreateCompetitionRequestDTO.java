package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.validation.annotation.ValidCompetitionRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
@ValidCompetitionRequestDTO
public record CreateCompetitionRequestDTO(
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
        Integer participants,
        @NotNull(message = "Simulator values cannot be null")
        @Valid
        SimulationValues simulationValues,
        @NotNull(message = "Relegation spots amount cannot be null")
        Integer relegationSpots
) {

    public Integer participants() {
        return ObjectUtils.defaultIfNull(participants, 0);
    }

    public List<Long> participantIds() {
        return ObjectUtils.defaultIfNull(participantIds, Collections.emptyList());
    }

    public int participantsAmount() {
        int participantIdsSize = participantIds == null ? 0 : participantIds.size();
        return Math.max(participantIdsSize, participants());
    }
}
