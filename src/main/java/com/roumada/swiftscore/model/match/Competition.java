package com.roumada.swiftscore.model.match;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("competition")
public class Competition {

    @Id
    private Long id = null;
    private int currentRoundNumber = 1;
    private String name;
    private CompetitionType type;
    private CountryCode country;
    private SimulationValues simulationValues;
    @DBRef
    private List<FootballClub> participants;
    @DBRef
    private List<CompetitionRound> rounds;

    @Builder
    private Competition(
            String name,
            CompetitionType type,
            CountryCode country,
            SimulationValues simulationValues,
            List<FootballClub> participants,
            List<CompetitionRound> rounds) {
        this.name = name;
        this.type = type;
        this.country = country;
        this.simulationValues = simulationValues;
        this.participants = participants;
        this.rounds = rounds;
    }

    public boolean canSimulate() {
        return rounds.size() + 1 > currentRoundNumber;
    }

    public CompetitionRound currentRound() {
        return rounds.get(currentRoundNumber - 1);
    }

    public void incrementCurrentRoundNumber() {
        currentRoundNumber++;
    }

    public enum CompetitionType {
        LEAGUE, TOURNAMENT
    }
}
