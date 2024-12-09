package com.roumada.swiftscore.competition.schedule;

import com.roumada.swiftscore.model.FootballClub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.roumada.swiftscore.competition.schedule.CompetitionRoundsGenerator.generateForLeague;
import static com.roumada.swiftscore.util.FootballClubTestUtils.generateFootballClubs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionRoundsGeneratorTests {

    @Test
    @DisplayName("Should correctly generate matchups for a league in round-robin system")
    void shouldCorrectlyGenerateRoundRobinLeague() {
        // arrange
        List<FootballClub> clubs = generateFootballClubs();

        // act
        var competition = generateForLeague(clubs);

        // assert
        assertEquals(clubs.size() * 2 - 2, competition.getRounds().size());
        assertThat(competition.getRounds()).doesNotHaveDuplicates();
    }
}
