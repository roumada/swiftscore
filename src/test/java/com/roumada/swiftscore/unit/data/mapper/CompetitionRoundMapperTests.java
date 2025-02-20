package com.roumada.swiftscore.unit.data.mapper;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.mapper.CompetitionRoundMapper;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionRoundMapperTests {
    private final CompetitionRoundMapper mapper = CompetitionRoundMapper.INSTANCE;

    @Test
    @DisplayName("Should map from round to response")
    void shouldMap() {
        long compId = 100L;
        long matchId = 1000L;
        long roundId = 10000L;
        List<FootballClub> clubs = FootballClubTestUtils.getTwoFootballClubs();
        FootballMatch fm = new FootballMatch(
                LocalDateTime.of(LocalDate.of(2025, 1, 1), LocalTime.of(21, 0)),
                clubs.get(0),
                clubs.get(1));
        fm.setId(matchId);
        fm.setCompetitionId(compId);
        fm.setHomeSideGoalsScored(5);
        fm.setAwaySideGoalsScored(2);
        fm.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        CompetitionRound round = CompetitionRound.builder()
                .competitionId(1L)
                .round(1)
                .matches(List.of(fm))
                .build();
        round.setId(roundId);

        var mapped = mapper.roundToResponseDTO(round);
        var mappedMatch = mapped.matches().get(0);
        assertEquals(round.getId(), mapped.id());
        assertEquals(round.getRound(), mapped.round());
        assertEquals(fm.getId(), mappedMatch.id());
        assertEquals(fm.getCompetitionId(), mappedMatch.competitionId());
        assertEquals(fm.getMatchResult(), mappedMatch.matchResult());
        assertEquals(fm.getDate(), mappedMatch.date());
        assertEquals(clubs.get(0), mappedMatch.homeSide());
        assertEquals(clubs.get(1), mappedMatch.awaySide());
        assertEquals(5, mappedMatch.homeSideGoalsScored());
        assertEquals(2, mappedMatch.awaySideGoalsScored());
    }
}
