package com.roumada.swiftscore.model.match;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match")
public class FootballMatch {

    @Id
    private Long id = null;
    private Long competitionId;
    private Long competitionRoundId;
    @DBRef
    private FootballMatchStatistics homeSideStatistics;
    @DBRef
    private FootballMatchStatistics awaySideStatistics;
    private Result matchResult = Result.UNFINISHED;

    public FootballMatch(FootballMatchStatistics homeSideStatistics, FootballMatchStatistics awaySideStatistics) {
        this.homeSideStatistics = homeSideStatistics;
        this.awaySideStatistics = awaySideStatistics;
    }

    public double getHomeSideVictoryChance() {
        return homeSideStatistics.getFootballClub().getVictoryChance();
    }

    public double getAwaySideVictoryChance() {
        return awaySideStatistics.getFootballClub().getVictoryChance();
    }

    public enum Result {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
