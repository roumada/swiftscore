package com.roumada.swiftscore.integration.controller;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class StatisticsControllerTests extends AbstractBaseIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionService compService;
    @Autowired
    private CompetitionDataLayer compdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Get competition statistics - with valid competition ID - should return")
    void getCompetitionStatistics_validCompetitionId_shouldReturn() throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(fcdl.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));
        var comp = compService.generateAndSave(new CreateCompetitionRequestDTO("",
                        CountryCode.GB,
                        "2025-01-01",
                        "2025-12-30",
                        ids,
                        null,
                        new SimulationValues(0.0)))
                .get();
        var compId = compdl.save(comp).getId();

        // act & assert
        mvc.perform(get("/statistics/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get competition statistics - with invalid competition ID - should return error code")
    void getCompetitionStatistics_invalidCompetitionId_shouldReturnError() throws Exception {
        // act & assert
        mvc.perform(get("/statistics/competition/" + -1)).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get club statistics - with valid club ID - should return")
    void getClubStatistics_validClubId_shouldReturn() throws Exception {
        // arrange
        var id = fcdl.save(FootballClub.builder().name("FC1").victoryChance(1).build()).getId();

        // act & assert
        mvc.perform(get("/statistics/club/" + id)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get club statistics - with invalid club ID - should return error code")
    void getClubStatistics_invalidClubId_shouldReturnError() throws Exception {
        // act & assert
        mvc.perform(get("/statistics/club/" + -1)).andExpect(status().is4xxClientError());
    }
}
