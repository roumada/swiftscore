package com.roumada.swiftscore.integration.logic.data;


import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        var comp = service.generateAndSave(new CompetitionRequestDTO("",
                Competition.CompetitionType.LEAGUE,
                CountryCode.GB,
                ids,
                new SimulationValues(0))).get();

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
                assertNotEquals(FootballMatch.MatchResult.UNFINISHED, simulatedRound.getMatches().get(a).getMatchResult());
            }
        }
    }

    @Test
    @DisplayName("Should generate and save a competition and all underlying objects to the database")
    void saveCompetition_allIdsShouldBeNonNullAndReferToCorrectObjects() {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.2f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fc1 = fcdl.save(fc1);
        fc2 = fcdl.save(fc2);
        var dto = new CompetitionRequestDTO("", Competition.CompetitionType.LEAGUE, CountryCode.GB,
                List.of(fc1.getId(), fc2.getId()), new SimulationValues(0));

        // act
        var optionalCompId = service.generateAndSave(dto);

        // assert
        assert (optionalCompId).isRight();
        var optionalComp = cdl.findCompetitionById(optionalCompId.get().getId());
        assert (optionalComp).isPresent();
        var comp = optionalComp.get();
        Long compId = comp.getId();
        assertNotNull(comp.getId());
        assertEquals(List.of(fc1, fc2), comp.getParticipants());
        for (CompetitionRound cr : comp.getRounds()) {
            assertNotNull(cr.getId());
            assertEquals(cr.getCompetitionId(), compId);
            for (FootballMatch fm : cr.getMatches()) {
                assertNotNull(fm.getId());
                assertNotNull(fm.getCompetitionId());
                assertEquals(fm.getCompetitionId(), compId);
                assertNotNull(fm.getCompetitionRoundId());
                assertEquals(fm.getCompetitionRoundId(), cr.getId());
            }
        }
    }
}
