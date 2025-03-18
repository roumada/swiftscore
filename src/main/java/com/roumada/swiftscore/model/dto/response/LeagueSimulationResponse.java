package com.roumada.swiftscore.model.dto.response;

import java.util.List;

public record LeagueSimulationResponse(Long leagueId,
                                       List<Long> competitionIds,
                                       List<Integer> simulatedTimes) {

}
