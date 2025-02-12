package com.roumada.swiftscore.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;

import java.time.LocalDate;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionResponseDTO(
        Long id,
        int lastSimulatedRound,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String season,
        CountryCode country,
        SimulationValues simulationValues,
        int relegationSpots,
        List<Long> participantIds,
        List<Long> roundIds
) {
}
