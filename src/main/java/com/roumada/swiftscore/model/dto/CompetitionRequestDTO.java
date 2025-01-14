package com.roumada.swiftscore.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roumada.swiftscore.model.SimulatorValues;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionRequestDTO(
        @NotNull(message = "Participant IDs list cannot be null")
        List<Long> participantIds,
        @NotNull(message = "Simulator values cannot be null")
        @Valid
        SimulatorValues simulatorValues
) {
}
