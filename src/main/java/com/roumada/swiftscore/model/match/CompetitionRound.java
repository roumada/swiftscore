package com.roumada.swiftscore.model.match;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("competition_round")
public class CompetitionRound {

    @Id
    private Long id = null;
    private Long competitionId;
    private int round;
    @DBRef
    private List<FootballMatch> matches;

    @Builder
    public CompetitionRound(Long competitionId, int round, List<FootballMatch> matches) {
        this.competitionId = competitionId;
        this.round = round;
        this.matches = matches;
    }

    public CompetitionRound(int round, List<FootballMatch> matches) {
        this(null, round, matches);
    }
}
