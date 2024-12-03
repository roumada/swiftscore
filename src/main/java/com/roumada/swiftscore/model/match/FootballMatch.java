package com.roumada.swiftscore.model.match;

import lombok.Data;

@Data
public class FootballMatch {
    private FootballClubMatchStatistics homeSideStatistics;
    private FootballClubMatchStatistics awaySideStatistics;
    private Result matchResult = Result.UNFINISHED;

    public FootballMatch(FootballClubMatchStatistics homeSideStatistics, FootballClubMatchStatistics awaySideStatistics) {
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
