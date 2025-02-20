package com.roumada.swiftscore.model.dto.response;

import java.util.List;

public record CompetitionSimulationResponse(long competitionId,
                                            int simulatedUntil,
                                            List<CompetitionRoundResponse> rounds) {
}
