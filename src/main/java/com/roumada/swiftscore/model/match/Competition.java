package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
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
    private int currentRound = 1;
    @DBRef
    private List<FootballClub> participants;
    @DBRef
    private List<CompetitionRound> rounds;

    public Competition(List<FootballClub> participants, List<CompetitionRound> rounds) {
        this.participants = participants;
        this.rounds = rounds;
    }

    public enum VarianceType {NONE, SIMPLE}
}
