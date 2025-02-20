package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationParameters;
import jakarta.validation.Valid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record UpdateCompetitionRequest(
        String name,
        CountryCode country,
        Integer relegationSpots,
        @Valid
        SimulationParameters simulationParameters
) {
}
