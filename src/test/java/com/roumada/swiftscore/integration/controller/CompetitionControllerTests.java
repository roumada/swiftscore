package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.data.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionDataLayer compdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Should create a competition if there are clubs with given IDs in the database")
    void shouldCreateCompetitionFromExistingClubs() throws Exception {
        // arrange
        fcdl.saveAll(FootballClubTestUtils.getFourFootballClubs());

        // act
        var mvcResult = mvc.perform(post("/competition").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO(
                                List.of(1L, 2L, 3L, 4L),
                                0.0f))))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse()
                .getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + -1))
                .andExpect(status().is4xxClientError());

        mvc.perform(get("/competition/" + compId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return all competitions")
    void shouldReturnAllCompetitions() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1L, 1, Collections.emptyList());
        var round2 = new CompetitionRound(2L, 1, Collections.emptyList());
        compdl.saveCompetitionRound(round1);
        compdl.saveCompetitionRound(round2);
        compdl.saveCompetition(new Competition(FootballClubTestUtils.getFourFootballClubs(),
                List.of(round1), 0.0f));
        compdl.saveCompetition(new Competition(FootballClubTestUtils.getFourFootballClubs(),
                List.of(round2), 0.0f));

        // act
        var result = mvc.perform(get("/competition/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var resultArray = new JSONArray(result);

        // assert
        assertEquals(2, resultArray.length());
    }

    @Test
    @DisplayName("Should return simulated round")
    void shouldReturnSimulatedRound() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().id(1L).name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().id(2L).name("FC2").victoryChance(0.4f).build();
        fcdl.save(fc1);
        fcdl.save(fc2);

        var round = new CompetitionRound(1L, 1,
                List.of(new FootballMatch(
                        new FootballMatchStatistics(fc1),
                        new FootballMatchStatistics(fc2))));
        compdl.saveCompetitionRound(round);

        var saved = compdl.saveCompetition(new Competition(
                List.of(fc1, fc2), List.of(round), 0));

        // act
        var response = mvc.perform(get("/competition/%s/simulate".formatted(saved.getId())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mvc.perform(get("/competition/-1/simulate"))
                .andExpect(status().is4xxClientError());

        // assert
        var responseJSON = new JSONObject(response);

        assertEquals(1, responseJSON.getJSONArray("matches").length());
    }
}
