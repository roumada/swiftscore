package com.roumada.swiftscore.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CreateFootballClubRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,
        @NotNull(message = "Country cannot be null")
        CountryCode country,
        @NotEmpty(message = "Stadium name cannot be empty")
        String stadiumName,
        @DecimalMin(value = "0.0", message = "Victory chance cannot be lower than 0")
        @DecimalMax(value = "1.0", message = "Victory chance cannot be greater than 1")
        @NotNull(message = "Victory chance cannot be null")
        double victoryChance) {
}
