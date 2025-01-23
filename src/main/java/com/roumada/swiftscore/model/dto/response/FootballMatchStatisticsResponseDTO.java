package com.roumada.swiftscore.model.dto.response;

import java.time.LocalDateTime;

public record FootballMatchStatisticsResponseDTO(Long footballMatchId,
                                                 LocalDateTime date,
                                                 int goalsScored,
                                                 int opponentGoalsScored) {
}
