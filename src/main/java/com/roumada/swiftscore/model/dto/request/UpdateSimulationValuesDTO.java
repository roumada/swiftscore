package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record UpdateSimulationValuesDTO(
        @DecimalMin(value = "0.0", message = "Variance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Variance cannot be higher than 1")
        Double variance,
        @DecimalMin(value = "0.0", message = "Score difference draw trigger cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Score difference draw trigger cannot be higher than 1")
        Double scoreDifferenceDrawTrigger,
        @DecimalMin(value = "0.0", message = "Draw trigger chance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Draw trigger chance cannot be higher than 1")
        Double drawTriggerChance) {
}
