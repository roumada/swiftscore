package com.roumada.swiftscore.model.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record FootballMatchResponseDTO(long id,
                                       FootballClub homeSide,
                                       FootballClub awaySide,
                                       FootballMatch.MatchResult matchResult,
                                       int homeSideGoalsScored,
                                       int awaySideGoalsScored,
                                       double homeSideCalculatedVictoryChance,
                                       double awaySideCalculatedVictoryChance) {
}
