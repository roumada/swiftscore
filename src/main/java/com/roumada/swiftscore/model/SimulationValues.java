package com.roumada.swiftscore.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record SimulationValues(
        @DecimalMin(value = "0.0", message = "Variance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Variance cannot be greater than 1")
        @NotNull(message = "Variance value must be present")
        double variance,
        @DecimalMin(value = "0.0", message = "Score difference draw trigger cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Score difference draw trigger cannot be greater than 1")
        double scoreDifferenceDrawTrigger,
        @DecimalMin(value = "0.0", message = "Draw trigger chance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Draw trigger chance cannot be greater than 1")
        double drawTriggerChance) {

    public SimulationValues(double variance) {
        this(variance, 0, 0);
    }
}
