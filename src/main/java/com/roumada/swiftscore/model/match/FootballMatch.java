package com.roumada.swiftscore.model.match;

import lombok.Data;

@Data
public class FootballMatch {
    private FootballClubMatchStatistics homeSideStatistics;
    private FootballClubMatchStatistics awaySideStatistics;
    private Status matchStatus = Status.UNFINISHED;

    public FootballMatch(FootballClubMatchStatistics homeSideStatistics, FootballClubMatchStatistics awaySideStatistics) {
        this.homeSideStatistics = homeSideStatistics;
        this.awaySideStatistics = awaySideStatistics;
    }

    public enum Status {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
