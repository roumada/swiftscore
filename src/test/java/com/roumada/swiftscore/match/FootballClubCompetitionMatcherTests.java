package com.roumada.swiftscore.match;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.MatchWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FootballClubCompetitionMatcherTests {

    private final FootballClubCompetitionMatcher matcher = new FootballClubCompetitionMatcher();

    @Test
    @DisplayName("Should correctly generate matchups for a league in round-robin system")
    void shouldCorrectlyGenerateRoundRobinLeague() {
        // arrange
        List<FootballClub> clubs = generateFootballClubs();

        // act
        List<MatchWeek> matchWeeks = matcher.generateScheduleForRoundRobinLeague(clubs);

        // assert
        assertEquals(clubs.size() * 2 - 2, matchWeeks.size());
        assertThat(matchWeeks).doesNotHaveDuplicates();
    }

    private List<FootballClub> generateFootballClubs() {
        return List.of(
                FootballClub.builder()
                        .name("FC1")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC2")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC3")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC4")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC5")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC6")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC7")
                        .victoryChance(1)
                        .build(),
                FootballClub.builder()
                        .name("FC8")
                        .victoryChance(1)
                        .build()
        );
    }

}
