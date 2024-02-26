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
    @DisplayName("Should generate an adequate number of unique matchups for given clubs")
    void shouldGenerateAllPossibleMatchesForFootballClubs() {
        // arrange
        List<FootballClub> clubs = generateFootballClubs();

        // act
        List<MatchWeek> matchWeeks = matcher.matchFor(clubs);

        // assert
        assertEquals(8 * 2 - 2, clubs.size() * 2 - 2);
        assertThat(clubs).doesNotHaveDuplicates();
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
