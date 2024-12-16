package com.roumada.swiftscore.unit.competition.schedule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.roumada.swiftscore.logic.competition.schedule.CompetitionRoundsGenerator.generate;
import static com.roumada.swiftscore.util.FootballClubTestUtils.getTenFootballClubs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionRoundsGeneratorTests {

    @Test
    @DisplayName("Should correctly generate matchups for a league in round-robin system")
    void shouldCorrectlyGenerateRoundRobinLeague() {
        // arrange
        var clubs = getTenFootballClubs();

        // act
        var rounds = generate(clubs);

        // assert
        assertEquals(clubs.size() * 2 - 2, rounds.size());
        assertThat(rounds).doesNotHaveDuplicates();
    }
}
