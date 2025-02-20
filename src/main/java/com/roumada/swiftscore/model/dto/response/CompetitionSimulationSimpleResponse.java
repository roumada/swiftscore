package com.roumada.swiftscore.model.dto.response;

import java.util.List;

public record CompetitionSimulationSimpleResponse(long competitionId,
                                                  int simulatedUntil,
                                                  List<Long> roundIds) {

}
