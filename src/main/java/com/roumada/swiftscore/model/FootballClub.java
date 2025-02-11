package com.roumada.swiftscore.model;

import com.neovisionaries.i18n.CountryCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "football_club")
@CompoundIndex(def = "{'name': 1, 'country': 1, 'stadiumName': 1}")
public class FootballClub {

    @Id
    private Long id = null;
    private String name;
    private CountryCode country;
    private String stadiumName;
    private double victoryChance;

    @Builder
    private FootballClub(String name, CountryCode country, String stadiumName, double victoryChance) {
        this.name = name;
        this.country = country;
        this.stadiumName = stadiumName;
        this.victoryChance = victoryChance;
    }
}

