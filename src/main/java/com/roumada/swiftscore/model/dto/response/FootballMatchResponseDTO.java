package com.roumada.swiftscore.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record FootballMatchResponseDTO(long id,
                                       LocalDateTime date,
                                       FootballClub homeSide,
                                       FootballClub awaySide,
                                       FootballMatch.MatchResult matchResult,
                                       int homeSideGoalsScored,
                                       int awaySideGoalsScored,
                                       double homeSideCalculatedVictoryChance,
                                       double awaySideCalculatedVictoryChance) {
}
