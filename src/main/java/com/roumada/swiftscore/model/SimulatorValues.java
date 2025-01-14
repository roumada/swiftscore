package com.roumada.swiftscore.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record SimulatorValues(
        @Size(min = 0, max = 1, message = "Variance cannot be lower than 0 and higher than 1")
        @NotNull(message = "Variance value must be present")
        double variance,
        @Size(min = 0, max = 1, message = "Score difference draw trigger cannot be lower than 0 and higher than 1")
        double scoreDifferenceDrawTrigger,
        @Size(min = 0, max = 1, message = "Draw trigger chance cannot be lower than 0 and higher than 1")
        double drawTriggerChance) {

    public SimulatorValues(double variance) {
        this(variance, 0, 0);
    }
}
