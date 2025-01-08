package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class StandingsControllerTests extends AbstractBaseIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionDataLayer compdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Get standings - with valid competition IDs - should return")
    void getStandings_validCompetitionId_shouldReturn() throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(
                fcdl.saveAll(FootballClubTestUtils.getFourFootballClubs()));
        var comp = compdl.generateAndSave(new CompetitionRequestDTO(ids, 0.0)).get();
        var compId = compdl.saveCompetition(comp).getId();

        // act & assert
        mvc.perform(get("/standings/" + compId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get standings - with invalid competition IDs - should return error code")
    void getStandings_invalidCompetitionId_shouldReturnError() throws Exception {
        // act & assert
        mvc.perform(get("/standings/" + 111))
                .andExpect(status().is4xxClientError());
    }
}
