package com.roumada.swiftscore.model.dto.request;

import java.util.List;

public record CreateLeagueCompetitionRequest(
        int tier,
        CreateCompetitionRequestDTO competition
) {
}
