package com.roumada.swiftscore.model.dto.request;

import java.util.List;

public record CreateLeagueRequest(
        String name,
        String startDate,
        String endDate,
        List<CreateLeagueCompetitionRequest> competitions
) {
}
