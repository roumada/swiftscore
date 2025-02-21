package com.roumada.swiftscore.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record SimulationParameters(
        @DecimalMin(value = "0.0", message = "Variance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Variance cannot be greater than 1")
        Double variance,
        @DecimalMin(value = "0.0", message = "Score difference draw trigger cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Score difference draw trigger cannot be greater than 1")
        Double scoreDifferenceDrawTrigger,
        @DecimalMin(value = "0.0", message = "Draw trigger chance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Draw trigger chance cannot be greater than 1")
        Double drawTriggerChance) {

    public SimulationParameters() {
        this(0., 0., 0.);
    }

    public SimulationParameters(double variance) {
        this(variance, 0., 0.);
    }
}
