package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompetitionDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubDataLayer fcDataLayer;
    @Autowired
    private CompetitionDataLayer dataLayer;

    @Test
    @DisplayName("Should generate and save a competition and all underlying objects to the database")
    void saveCompetition_allIdsShouldBeNonNullAndReferToCorrectObjects() {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.2f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fc1 = fcDataLayer.save(fc1);
        fc2 = fcDataLayer.save(fc2);
        var dto = new CompetitionRequestDTO(List.of(fc1.getId(), fc2.getId()), 0.0);

        // act
        var optionalCompId = dataLayer.generateAndSave(dto);

        // assert
        assert (optionalCompId).isRight();
        var optionalComp = dataLayer.findCompetitionById(optionalCompId.get().getId());
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
