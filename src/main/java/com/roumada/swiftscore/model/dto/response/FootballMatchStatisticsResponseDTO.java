package com.roumada.swiftscore.model.dto.response;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;

public record FootballMatchStatisticsResponseDTO(Long footballMatchId,
                                                 FootballMatchStatistics.MatchStatisticsResult result,
                                                 int goalsScored,
                                                 int opponentGoalsScored) {
}
