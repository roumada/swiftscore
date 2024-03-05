package com.roumada.swiftscore.competition.schedule;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.CompetitionRound;
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
        List<CompetitionRound> competitionRounds = generateForLeague(clubs);

        // assert
        assertEquals(clubs.size() * 2 - 2, competitionRounds.size());
        assertThat(competitionRounds).doesNotHaveDuplicates();
    }
}
