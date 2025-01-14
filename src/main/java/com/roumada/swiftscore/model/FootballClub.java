package com.roumada.swiftscore.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "football_club")
public class FootballClub {

    @Id
    private Long id = null;
    private String name;
    private double victoryChance;

    @Builder
    private FootballClub(String name, double victoryChance) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Club should have a name");
        if (victoryChance > 1) throw new IllegalArgumentException("Victory chance cannot exceed 1");
        if (victoryChance < 0) throw new IllegalArgumentException("Victory chance cannot be lower than 0");

        this.name = name;
        this.victoryChance = victoryChance;
    }
}

