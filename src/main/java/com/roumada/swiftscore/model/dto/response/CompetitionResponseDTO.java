package com.roumada.swiftscore.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.match.Competition;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionResponseDTO(
        Long id,
        int currentRound,
        String name,
        Competition.CompetitionType type,
        CountryCode country,
        SimulationValues simulationValues,
        List<Long> participantIds,
        List<Long> roundIds
) {
}
