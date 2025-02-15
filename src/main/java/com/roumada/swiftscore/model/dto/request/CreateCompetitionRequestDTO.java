package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionParametersDTO;
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

        @NotNull(message = "Country code cannot be null")
        CountryCode country,

        @NotNull(message = "Start date cannot be null")
        String startDate,

        @NotNull(message = "End date cannot be null")
        String endDate,

        @NotNull(message = "Parameters cannot be null")
        CompetitionParametersDTO parameters,

        @Valid
        @NotNull(message = "Simulator values cannot be null")
        SimulationValues simulationValues
) {
    public Integer participants() {
        return ObjectUtils.defaultIfNull(parameters.participants(), 0);
    }

    public List<Long> participantIds() {
        return ObjectUtils.defaultIfNull(parameters.participantIds(), Collections.emptyList());
    }

    public int participantsAmount() {
        int participantIdsSize = parameters.participantIds() == null ? 0 : parameters.participantIds().size();
        return Math.max(participantIdsSize, participants());
    }
}
