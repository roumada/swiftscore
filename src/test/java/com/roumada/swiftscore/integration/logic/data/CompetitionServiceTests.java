package com.roumada.swiftscore.integration.logic.data;


import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.logic.data.CompetitionService;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionServiceTests extends AbstractBaseIntegrationTest {

    @Autowired
    private CompetitionService service;

    @Autowired
    private CompetitionDataLayer cdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Simulate competition - for a competition with four clubs - " +
            "matches should be properly resolved")
    void generateStandings_forFullySimulatedCompetition_shouldReturnResolved() {
        // arrange
        var clubs = FootballClubTestUtils.getFourFootballClubs();
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(fcdl.saveAll(clubs));
        var comp = cdl.generateAndSave(new CompetitionRequestDTO(ids, 0.0)).get();

        for (int i = 0; i < clubs.size(); i++) {
            // act
            var eitherSimulatedRound = service.simulateRound(comp);

            // assert
            assert (eitherSimulatedRound).isRight();

            var simulatedRound = eitherSimulatedRound.get();
            assertNotNull(simulatedRound);
            assertEquals(i + 1, simulatedRound.getRound());
            assertEquals(comp.getId(), simulatedRound.getCompetitionId());

            for (int a = 0; a < clubs.size() / 2; a++) {
                assertNotEquals(FootballMatch.Result.UNFINISHED, simulatedRound.getMatches().get(a).getMatchResult());
            }
        }
    }
}
