package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository fcr;
    @Autowired
    private CompetitionRepository cr;

    @Autowired
    private CompetitionDataLayer competitionDataLayer;

    @Test
    @DisplayName("Save competition - should save")
    void saveCompetition_shouldSave() {
        // arrange
        var fcs = fcr.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = Competition.builder()
                .name("Competition")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build();

        // act
        var id = competitionDataLayer.save(competition).getId();

        // assert
        assertTrue(cr.findById(id).isPresent());
    }

    @Test
    @DisplayName("Find a competition - should find")
    void findACompetition_shouldFind() {
        // arrange
        var fcs = fcr.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var saved = cr.save(Competition.builder()
                .name("Competition")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build());

        // act
        var findResult = competitionDataLayer.findCompetitionById(saved.getId());

        // assert
        assertTrue(findResult.isPresent());
        var found = findResult.get();
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    @DisplayName("Find all competitions - should find")
    void findAllCompetitions_shouldFind() {
        // arrange
        var fcs = fcr.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var ids = cr.saveAll(List.of(Competition.builder()
                .name("Competition")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build(), Competition.builder()
                .name("Competition 2")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build())).stream().map(Competition::getId).toList();

        // act
        var comps = competitionDataLayer.findAllCompetitions();

        // assert
        assertEquals(2, comps.size());
        assertEquals(ids, comps.stream().map(Competition::getId).toList());
    }
}
