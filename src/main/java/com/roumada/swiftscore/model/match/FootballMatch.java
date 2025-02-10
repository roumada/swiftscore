package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("football_match")
@NoArgsConstructor
public class FootballMatch {

    @Id
    private Long id = null;
    private LocalDateTime date;
    private Long competitionId;
    private Long competitionRoundId;
    @DBRef
    private FootballClub homeSideFootballClub;
    private double homeSideCalculatedVictoryChance;
    private int homeSideGoalsScored;
    @DBRef
    private FootballClub awaySideFootballClub;
    private double awaySideCalculatedVictoryChance;
    private int awaySideGoalsScored;
    private MatchResult matchResult = MatchResult.UNFINISHED;
    private int extraVictorGoals;

    public FootballMatch(LocalDateTime date, FootballClub homeSideFootballClub, FootballClub awaySideFootballClub) {
        this.date = date;
        this.homeSideFootballClub = homeSideFootballClub;
        this.awaySideFootballClub = awaySideFootballClub;
    }

    public FootballMatch(FootballClub homeSideFootballClub, FootballClub awaySideFootballClub) {
        this(null, homeSideFootballClub, awaySideFootballClub);
    }

    public double getHomeSideVictoryChance() {
        return homeSideFootballClub.getVictoryChance();
    }

    public double getAwaySideVictoryChance() {
        return awaySideFootballClub.getVictoryChance();
    }

    public enum MatchResult {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
