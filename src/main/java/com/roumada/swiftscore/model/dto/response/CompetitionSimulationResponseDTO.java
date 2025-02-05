package com.roumada.swiftscore.model.dto.response;

import java.util.List;

public record CompetitionSimulationResponseDTO(long competitionId,
                                               int simulatedUntil,
                                               List<CompetitionRoundResponseDTO> rounds) {
}
