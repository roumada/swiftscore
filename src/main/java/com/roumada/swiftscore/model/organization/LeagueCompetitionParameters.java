package com.roumada.swiftscore.model.organization;

import lombok.Builder;

import java.util.List;

public class LeagueCompetitionParameters {
    private long leagueId;
    private int relegationSpots;
    private List<Long> previousCompetitions;

    @Builder
    public LeagueCompetitionParameters(long leagueId, int relegationSpots, List<Long> previousCompetitions) {
        this.leagueId = leagueId;
        this.relegationSpots = relegationSpots;
        this.previousCompetitions = previousCompetitions;
    }
}
