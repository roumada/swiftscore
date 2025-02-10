package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import jakarta.validation.Valid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record UpdateCompetitionRequestDTO(
        String name,
        CountryCode country,
        @Valid
        UpdateSimulationValuesDTO simulationValues
) {
}
