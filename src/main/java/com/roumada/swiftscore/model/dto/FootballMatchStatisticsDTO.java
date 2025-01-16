package com.roumada.swiftscore.model.dto;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;

public record FootballMatchStatisticsDTO(Long footballMatchId,
                                         FootballMatchStatistics.MatchStatisticsResult result,
                                         int goalsScored,
                                         int opponentGoalsScored) {
}
