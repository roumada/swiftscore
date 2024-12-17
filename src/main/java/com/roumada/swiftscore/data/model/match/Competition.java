package com.roumada.swiftscore.data.model.match;

import com.roumada.swiftscore.data.model.FootballClub;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("competition")
@NoArgsConstructor
public class Competition {

    @Id
    private Long id = null;
    private int currentRoundNumber = 1;
    @DBRef
    private List<FootballClub> participants;
    @DBRef
    private List<CompetitionRound> rounds;
    private float variance;

    @Builder
    public Competition(List<FootballClub> participants, List<CompetitionRound> rounds, float variance) {
        this.participants = participants;
        this.rounds = rounds;
        this.variance = variance;
    }

    public boolean canSimulate() {
        return rounds.size() + 1 > currentRoundNumber;
    }

    public CompetitionRound getCurrentRound() {
        return rounds.get(currentRoundNumber - 1);
    }

    public void incrementCurrentRoundNumber() {
        currentRoundNumber++;
    }
}
