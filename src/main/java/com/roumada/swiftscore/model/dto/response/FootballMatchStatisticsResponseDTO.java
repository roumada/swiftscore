package com.roumada.swiftscore.model.dto.response;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;

import java.time.LocalDateTime;

public record FootballMatchStatisticsResponseDTO(Long footballMatchId,
                                                 LocalDateTime date,
                                                 FootballMatchStatistics.MatchStatisticsResult result,
                                                 int goalsScored,
                                                 int opponentGoalsScored) {
}
