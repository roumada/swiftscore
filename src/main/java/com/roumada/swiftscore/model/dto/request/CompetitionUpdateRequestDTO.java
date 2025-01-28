package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.match.Competition;
import jakarta.validation.Valid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionUpdateRequestDTO(
        String name,
        Competition.CompetitionType type,
        CountryCode country,
        @Valid
        SimulationValues simulationValues
) {
}
