package com.roumada.swiftscore.integration.controller;

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
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private CompetitionDataLayer competitionDataLayer;
    @Autowired
    private FootballClubDataLayer footballClubDataLayer;

    @Test
    @DisplayName("Create competition - with valid football club IDs - should create")
    void createCompetition_validData_isCreated() throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs()));

        // act
        var mvcResult = mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CompetitionRequestDTO("",
                                Competition.CompetitionType.LEAGUE,
                                CountryCode.GB,
                                ids,
                                new SimulationValues(0)))))
                .andExpect(status().isOk()).andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create competition - with invalid football club IDs - should return error code")
    void createCompetition_invalidIds_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());

        // act
        mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CompetitionRequestDTO("",
                                Competition.CompetitionType.LEAGUE,
                                CountryCode.GB,
                                List.of(1L, 2L, 3L, 9L),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create competition  - with uneven football club ID count - should return error code")
    void createCompetition_invalidIdCount_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());

        // act
        mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                        Competition.CompetitionType.LEAGUE,
                        CountryCode.GB,
                        List.of(1L, 2L, 3L),
                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1}) // One parameter: int
    @DisplayName("Create competition  - with invalid variance value - should return error code")
    void createCompetition_invalidVarianceNumber_shouldReturnErrorCode(double variation) throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs()));

        // act
        mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                        Competition.CompetitionType.LEAGUE,
                        CountryCode.GB,
                        ids,
                        new SimulationValues(variation, 0.0, 0.0)))))
                .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1}) // One parameter: int
    @DisplayName("Create competition  - with invalid draw trigger chance value - should return error code")
    void createCompetition_invalidDrawTriggerChanceValue_shouldReturnErrorCode(double drawTriggerChance) throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs()));

        // act
        mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                        Competition.CompetitionType.LEAGUE,
                        CountryCode.GB,
                        ids,
                        new SimulationValues(0.0, 0.0, drawTriggerChance)))))
                .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1}) // One parameter: int
    @DisplayName("Create competition  - with invalid score diff draw trigger value - should return error code")
    void createCompetition_invalidScoreDiffDrawTriggerValue_shouldReturnErrorCode(double scoreDifferenceDrawTrigger) throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs()));

        // act
        mvc.perform(post("/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                        Competition.CompetitionType.LEAGUE,
                        CountryCode.GB,
                        ids,
                        new SimulationValues(0.0, scoreDifferenceDrawTrigger, 0.0)))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create competition  - with null name - should return error code")
    void createCompetition_withNullName_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());

        // act
        mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO(null,
                                Competition.CompetitionType.LEAGUE,
                                CountryCode.GB,
                                List.of(1L, 2L, 3L),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create competition  - with null competition type - should return error code")
    void createCompetition_withNullCompetitionType_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());

        // act
        mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                null,
                                CountryCode.GB,
                                List.of(1L, 2L, 3L),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get a competition - with valid ID - should return")
    void getCompetition_withValidID_shouldReturn() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1, Collections.emptyList());
        round1 = competitionDataLayer.saveCompetitionRound(round1);
        var savedClubs = footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());
        var id = competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition").type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(savedClubs)
                .rounds(List.of(round1))
                .build()).getId();

        // act
        var result = mvc.perform(get("/competition/" + id)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var resultJSON = new JSONObject(result);

        // assert
        assertEquals(id, resultJSON.getLong("id"));
    }

    @Test
    @DisplayName("Get a competitions - with invalid ID - should return error code")
    void getCompetition_withInvalidID_shouldReturnErrorCode() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1, Collections.emptyList());
        competitionDataLayer.saveCompetitionRound(round1);
        var savedClubs = footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());
        competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(savedClubs)
                .rounds(List.of(round1))
                .build());

        // act & assert
        mvc.perform(get("/competition/999")).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get all competitions - should return")
    void getAllCompetitions_shouldReturnAll() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1, Collections.emptyList());
        var round2 = new CompetitionRound(1, Collections.emptyList());
        competitionDataLayer.saveCompetitionRound(round1);
        competitionDataLayer.saveCompetitionRound(round2);
        var savedClubs = footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs());
        competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(savedClubs)
                .rounds(List.of(round1))
                .build());
        competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(savedClubs)
                .rounds(List.of(round2))
                .build());

        // act
        var result = mvc.perform(get("/competition/all")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var resultArray = new JSONArray(result);

        // assert
        assertEquals(2, resultArray.length());
    }

    @Test
    @DisplayName("Simulate competition  - is still simulable - should simulate and return simulated round")
    void simulateCompetitionRound_isStillSimulable_shouldSimulateAndReturn() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);

        var round = new CompetitionRound(null, 1, List.of(new FootballMatch(fc1, fc2)));
        competitionDataLayer.saveCompetitionRound(round);

        var saved = competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round))
                .build());

        // act
        var response = mvc.perform(get("/competition/%s/simulate".formatted(saved.getId())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        mvc.perform(get("/competition/-1/simulate")).andExpect(status().is4xxClientError());

        // assert
        var responseJSON = new JSONObject(response);

        assertEquals(1, responseJSON.getJSONArray("matches").length());
    }

    @Test
    @DisplayName("Simulate competition  - is originally simulable - should return error code when no longer simulable")
    void simulateCompetitionRound_isOriginallySimulable_shouldReturnErrorCodeWhenNoLongerSimulable() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);

        var round = new CompetitionRound(1, List.of(new FootballMatch(fc1, fc2)));
        competitionDataLayer.saveCompetitionRound(round);

        var saved = competitionDataLayer.saveCompetition(Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round))
                .build());

        // act
        mvc.perform(get("/competition/%s/simulate".formatted(saved.getId()))).andExpect(status().isOk());
        mvc.perform(get("/competition/%s/simulate".formatted(saved.getId()))).andExpect(status().is4xxClientError());
    }
}
