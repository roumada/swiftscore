package com.roumada.swiftscore.model.match;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match")
public class FootballMatch {

    @Id
    private Long id;
    private FootballMatchStatistics homeSideStatistics;
    private FootballMatchStatistics awaySideStatistics;
    private Result matchResult = Result.UNFINISHED;

    public FootballMatch(FootballMatchStatistics homeSideStatistics, FootballMatchStatistics awaySideStatistics) {
        this.homeSideStatistics = homeSideStatistics;
        this.awaySideStatistics = awaySideStatistics;
    }

    public float getHomeSideVictoryChance() {
        return homeSideStatistics.getFootballClub().getVictoryChance();
    }

    public float getAwaySideVictoryChance() {
        return awaySideStatistics.getFootballClub().getVictoryChance();
    }

    public enum Result {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
