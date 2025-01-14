package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulatorValues;
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
    private SimulatorValues simulatorValues;
    @DBRef
    private List<FootballClub> participants;
    @DBRef
    private List<CompetitionRound> rounds;

    @Builder
    public Competition(SimulatorValues simulatorValues, List<FootballClub> participants, List<CompetitionRound> rounds) {
        this.simulatorValues = simulatorValues;
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
}
