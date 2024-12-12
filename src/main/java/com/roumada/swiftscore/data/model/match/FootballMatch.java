package com.roumada.swiftscore.data.model.match;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match")
@NoArgsConstructor
public class FootballMatch {

    @Id
    private Long id = null;
    @DBRef
    private FootballMatchStatistics homeSideStatistics;
    @DBRef
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
