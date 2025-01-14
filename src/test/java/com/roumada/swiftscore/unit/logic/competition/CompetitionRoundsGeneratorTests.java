package com.roumada.swiftscore.unit.logic.competition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator.generate;
import static com.roumada.swiftscore.util.FootballClubTestUtils.getTenFootballClubs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionRoundsGeneratorTests {

    @Test
    @DisplayName("Should correctly generate matchups for a league in round-robin system")
    void shouldCorrectlyGenerateRoundRobinLeague() {
        // arrange
        var clubs = getTenFootballClubs();

        // act
        var roundsEither = generate(clubs);

        // assert
        assertTrue(roundsEither.isRight());
        var rounds = roundsEither.get();
        assertEquals(clubs.size() * 2 - 2, rounds.size());
        assertThat(rounds).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Generate clubs - incorrect clubs amount - should return left Either with error message")
    void generateClubs_incorrectClubAmount_shouldReturnErrorMessage() {
        // arrange
        var clubs = getTenFootballClubs().subList(0, 5);

        // act
        var rounds = generate(clubs);

        // assert
        assertTrue(rounds.isLeft());
        assertEquals("Unable to generate competition rounds for odd amount of clubs.", rounds.getLeft());
    }
}
