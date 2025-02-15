package com.roumada.swiftscore.model.dto.response;

import java.util.List;

public record CompetitionStandingsResponseDTO(
        List<FootballClubStandings> standings,
        List<Long> retained,
        List<Long> relegated
) {
}
