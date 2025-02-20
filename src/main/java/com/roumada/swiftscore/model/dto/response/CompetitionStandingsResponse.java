package com.roumada.swiftscore.model.dto.response;

import java.util.List;


public record CompetitionStandingsResponse(
        List<FootballClubStandings> standings,
        List<Long> retained,
        List<Long> relegated
) {
}
