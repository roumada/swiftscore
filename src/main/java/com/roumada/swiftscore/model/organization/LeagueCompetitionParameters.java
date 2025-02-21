package com.roumada.swiftscore.model.organization;

import lombok.Builder;

import java.util.List;

@Builder
public class LeagueCompetitionParameters {
    private long leagueId;
    private int tier;
    private int relegationSpots;
    private List<Long> previousCompetitions;
}
