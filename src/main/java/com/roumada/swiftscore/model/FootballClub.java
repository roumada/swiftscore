package com.roumada.swiftscore.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
public class FootballClub {
    private String name;
    private float victoryChance;

    public static class Builder {
        public FootballClub build() {
            if (victoryChance > 1) {
                throw new IllegalArgumentException("Victory chance cannot exceed 1");
            }
            if (victoryChance < 0) {
                throw new IllegalArgumentException("Victory chance cannot be lower than 0");
            }
            return new FootballClub(name, victoryChance);
        }
    }
}
