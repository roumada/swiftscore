package com.roumada.swiftscore.integration.controller;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionParametersDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
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
                        new CompetitionParametersDTO(0, ids, 0),
                        new SimulationValues(0)))
                .get();
        var compId = compdl.save(comp).getId();

        // act & assert
        mvc.perform(get("/statistics/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get competition statistics - with invalid competition ID - should return error code")
    void getCompetitionStatistics_invalidCompetitionId_shouldReturnError() throws Exception {
        // arrange
        var compId = 999;
        // act
        var response = mvc.perform(get("/statistics/competition/" + compId))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Couldn't find competition with ID [%s]".formatted(compId));
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
        // arrange
        var clubId = 999;
        // act
        var response = mvc.perform(get("/statistics/club/" + clubId))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Couldn't find club with ID [%s]".formatted(clubId));
    }
}
