package com.roumada.swiftscore.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionParametersDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateSimulationValuesDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionSimulationSimpleResponseDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionDataLayer competitionDataLayer;
    @Autowired
    private CompetitionRoundDataLayer competitionRoundDataLayer;
    @Autowired
    private FootballClubDataLayer footballClubDataLayer;
    @Autowired
    private FootballMatchDataLayer footballMatchDataLayer;

    @Test
    @DisplayName("Create competition - with valid football club IDs & football IDs only - should create")
    void createCompetition_validDataAndIdsOnly_isCreated() throws Exception {
        // arrange
        var ids = FootballClubTestUtils
                .getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var mvcResult = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(0, ids, 0),
                                        new SimulationValues(0))

                        )))
                .andExpect(status().isOk()).andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + compId)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 8})
    @DisplayName("Create competition - with valid football club IDs & set participants - should create")
    void createCompetition_validIdsAndFillToParticipantsSet_isCreated(int participants) throws Exception {
        // arrange
        var ids = FootballClubTestUtils
                .getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()), 4);

        // act
        var mvcResult = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(participants, ids, 0),
                                        new SimulationValues(0))
                        )))
                .andExpect(status().isOk()).andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create competition - with uneven football club IDs amount & even participants - should create")
    void createCompetition_unevenFootballClubIdsButEvenFillToParticipants_isCreated() throws Exception {
        // arrange
        var ids = FootballClubTestUtils
                .getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()), 3);

        // act
        var mvcResult = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(8, ids, 0),
                                        new SimulationValues(0))
                        )))
                .andExpect(status().isOk()).andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create competition - with uneven football club IDs amount & uneven participants - should return error code")
    void createCompetition_unevenFootballClubIdsButUnevenFillToParticipants_isCreated() throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()), 3);

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(7, ids, 0),
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError()).andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.", errorMsg);
    }

    @Test
    @DisplayName("Create competition - with even football club IDs amount & uneven participants - should return error code")
    void createCompetition_evenFootballClubIdsButUnevenFillToParticipants_isCreated() throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()), 4);

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(7, ids, 0),
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError()).andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.", errorMsg);
    }

    @Test
    @DisplayName("Create competition - with participants parameter only - should create")
    void createCompetition_withFillToParticipantsOnly_isCreated() throws Exception {
        // arrange
        FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()));

        // act
        var mvcResult = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(8, null, 0),
                                        new SimulationValues(0)))))
                .andExpect(status().isOk()).andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + compId)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create competition - with uneven participants parameter only - should create")
    void createCompetition_withUnevenFillToParticipantsOnly_isCreated() throws Exception {
        // arrange
        FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubs()));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        new CompetitionParametersDTO(7, null, 0),
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError()).andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.", errorMsg);
    }

    @Test
    @DisplayName("Create competition - with invalid football club IDs - should return error code")
    void createCompetition_invalidIds_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-30",
                                        new CompetitionParametersDTO(0, List.of(1L, 2L, 3L, 9L), 0),
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Couldn't retrieve all clubs for given IDs and country", errorMsg);
    }

    @Test
    @DisplayName("Create competition - invalid club countries - should return error code")
    void createCompetition_invalidClubCountries_shouldReturnErrorCode() throws Exception {
        // arrange
        var clubs = FootballClubTestUtils.getFourFootballClubs(false);
        clubs.get(0).setCountry(CountryCode.ES);
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(clubs));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-30",
                                        new CompetitionParametersDTO(0, ids, 0),
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Couldn't retrieve all clubs for given IDs and country", errorMsg);
    }

    @Test
    @DisplayName("Create competition  - with uneven football club ID count - should return error code")
    void createCompetition_invalidIdCount_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParametersDTO(0, List.of(1L, 2L, 3L), 0),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.",
                errorMsg);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Create competition  - with invalid variance value - should return error code")
    void createCompetition_invalidVarianceNumber_shouldReturnErrorCode(double variation) throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParametersDTO(0, ids, 0),
                                new SimulationValues(variation, 0.0, 0.0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        if (variation < 0) {
            assertEquals("Variance cannot be lower than 0",
                    validationErrors.get(0));
        } else {
            assertEquals("Variance cannot be higher than 1",
                    validationErrors.get(0));
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Create competition  - with invalid draw trigger chance value - should return error code")
    void createCompetition_invalidDrawTriggerChanceValue_shouldReturnErrorCode(double drawTriggerChance) throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-11-10",
                                new CompetitionParametersDTO(0, ids, 0),
                                new SimulationValues(0.0, 0.0, drawTriggerChance)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        if (drawTriggerChance < 0) {
            assertEquals("Draw trigger chance cannot be lower than 0",
                    validationErrors.get(0));
        } else {
            assertEquals("Draw trigger chance cannot be higher than 1",
                    validationErrors.get(0));
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Create competition  - with invalid score diff draw trigger value - should return error code")
    void createCompetition_invalidScoreDiffDrawTriggerValue_shouldReturnErrorCode(double scoreDifferenceDrawTrigger) throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParametersDTO(0, ids, 0),
                                new SimulationValues(0.0, scoreDifferenceDrawTrigger, 0.0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        if (scoreDifferenceDrawTrigger < 0) {
            assertEquals("Score difference draw trigger cannot be lower than 0",
                    validationErrors.get(0));
        } else {
            assertEquals("Score difference draw trigger cannot be higher than 1",
                    validationErrors.get(0));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'2025-01-01', '',  'End date must be present'",
            "'', 2025-01-01, 'Start date must be present'",
            "'', '', 'Start date must be present  + End date must be present'",
            "2025--01-01, '2025-02-02', 'Unparsable data format for one of the dates (must be YYYY-MM-DD)'",
            "'2025-01-01', '2025-01-02', 'Competition needs at least 6 days for a competition with 4 clubs.'",
            "'2025-02-01', '2025-01-01', 'Start date cannot be ahead of end date'",
            "'2020-01-01', '2025-01-01', 'The amount of days for a competition has exceed maximum duration [320]'"
    })
    @DisplayName("Create competition  - with invalid dates - should return error code")
    void createCompetition_invalidDates_shouldReturnErrorCode(String startDate,
                                                              String endDate,
                                                              String validationErrorMsg) throws Exception {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("",
                                CountryCode.GB,
                                startDate,
                                endDate,
                                new CompetitionParametersDTO(0, ids, 0),
                                new SimulationValues(0.0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Create competition - with null name - should return error code")
    void createCompetition_withNullName_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO(null,
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParametersDTO(4, null, 0),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertEquals("Name cannot be null", validationErrors.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    @DisplayName("Create competition - invalid relegation spots amount - should return error code")
    void createCompetition_invalidRelegationSpotsAmount_shouldReturnErrorCode(int relegationSpots) throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequestDTO("Competition",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParametersDTO(4, null, relegationSpots),
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertEquals("Amount of participants must be at least greater than one than relegateion spots", validationErrors.get(0));
    }

    @Test
    @DisplayName("Delete competition - should return OK")
    void deleteCompetition_shouldReturnOK() throws Exception {
        // arrange
        var id = competitionDataLayer.save(Competition.builder().build()).getId();

        // act
        mvc.perform(delete("/competition/%s".formatted(id))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get a competition - with valid ID - should return")
    void getCompetition_withValidID_shouldReturn() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1, Collections.emptyList());
        round1 = competitionRoundDataLayer.save(round1);
        var savedClubs = footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));
        var id = competitionDataLayer.save(Competition.builder()
                .name("Competition")
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
    @DisplayName("Get a competition - with invalid ID - should return error code")
    void getCompetition_withInvalidID_shouldReturnErrorCode() throws Exception {
        // act & assert
        mvc.perform(get("/competition/999")).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Simulate competition  - can still be simulated - should simulate and return simulated round")
    void simulateCompetitionRound_canStillBeSimulated_shouldSimulateAndReturn() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);
        var fm = new FootballMatch(fc1, fc2);
        fm = footballMatchDataLayer.save(fm);

        var round = new CompetitionRound(null, 1, List.of(fm));
        competitionRoundDataLayer.save(round);

        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round))
                .build());

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(saved.getId()))
                        .param("times", "1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);

        assertEquals(1, responseJSON.getJSONArray("rounds").length());
    }

    @Test
    @DisplayName("Simulate competition  - can be simulated - should return error code when no longer can")
    void simulateCompetitionRound_canBeSimulated_shouldReturnErrorCodeWhenNoLongerCan() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);

        FootballMatch fm = new FootballMatch(fc1, fc2);
        fm = footballMatchDataLayer.save(fm);

        var round = new CompetitionRound(1, List.of(fm));
        competitionRoundDataLayer.save(round);

        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round))
                .build());

        // act
        mvc.perform(post("/competition/%s/simulate".formatted(saved.getId())).param("times", "1")).andExpect(status().isOk());
        mvc.perform(post("/competition/%s/simulate".formatted(saved.getId()))).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Simulate competition  - can be simulated - should simulate multiple times with one request and return simulated round")
    void simulateCompetitionRound_canBeSimulated_shouldSimulateMultipleTimesAndReturn() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);
        var fm = new FootballMatch(fc1, fc2);
        fm = footballMatchDataLayer.save(fm);

        var round = new CompetitionRound(null, 1, List.of(fm));
        competitionRoundDataLayer.save(round);

        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round, round, round, round))
                .build());

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(saved.getId()))
                        .param("times", "3"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);

        assertEquals(3, responseJSON.getJSONArray("rounds").length());
    }

    @Test
    @DisplayName("Simulate competition  - can be simulated - should simulate multiple times with one request and return simulated round in simple form")
    void simulateCompetitionRound_canBeSimulated_shouldSimulateMultipleTimesAndReturnSimplifiedRequest() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.4f).build();
        footballClubDataLayer.save(fc1);
        footballClubDataLayer.save(fc2);
        var fm = new FootballMatch(fc1, fc2);
        fm = footballMatchDataLayer.save(fm);

        var round = new CompetitionRound(null, 1, List.of(fm));
        competitionRoundDataLayer.save(round);

        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round, round, round, round))
                .build());

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(saved.getId()))
                        .param("times", "3")
                        .param("simplify", "true"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // assert
        assertDoesNotThrow(() -> objectMapper.readValue(response, CompetitionSimulationSimpleResponseDTO.class));
    }

    @Test
    @DisplayName("Update competition - name only - should return updated")
    void updateCompetition_name_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO("New Competition",
                null,
                null,
                null);
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(dto.name(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(saved
                        .getSimulationValues()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(saved
                        .getSimulationValues()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
        assertEquals(saved
                        .getSimulationValues()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - country only - should return updated")
    void updateCompetition_country_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                CountryCode.SE,
                null,
                null);
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(dto.country(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(saved
                        .getSimulationValues()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(saved
                        .getSimulationValues()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
        assertEquals(saved
                        .getSimulationValues()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - simulation values only - should return updated")
    void updateCompetition_simValues_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(0.1, 0.2, 0.3));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(dto
                        .simulationValues()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(dto
                        .simulationValues()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
        assertEquals(dto
                        .simulationValues()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - variance only - should return updated")
    void updateCompetition_variance_shouldReturnUpdated() throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(0.1, null, null));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(dto
                        .simulationValues()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(simVals
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
        assertEquals(simVals
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Variance cannot be lower than 0'",
            "1.1, 'Variance cannot be higher than 1'",})
    @DisplayName("Update competition - variance only, invalid values - should return error code")
    void updateCompetition_invalidVarianceOnly_shouldReturnErrorCode(double variance, String validationErrorMsg) throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(variance, null, null));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - score diff draw trigger only - should return updated")
    void updateCompetition_sddt_shouldReturnUpdated() throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(null, 0.2, null));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(simVals
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(simVals
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
        assertEquals(dto
                        .simulationValues()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Score difference draw trigger cannot be lower than 0'",
            "1.1, 'Score difference draw trigger cannot be higher than 1'",})
    @DisplayName("Update competition - score diff draw trigger only, invalid values - should return error code")
    void updateCompetition_invalidSddt_shouldReturnUpdated(double sddt, String validationErrorMsg) throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(null, sddt, null));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - draw trigger chance only - should return updated")
    void updateCompetition_dtc_shouldReturnUpdated() throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(null, null, 0.2));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(simVals
                        .variance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("variance"));
        assertEquals(dto
                        .simulationValues()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("drawTriggerChance"));
        assertEquals(simVals
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationValues")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Draw trigger chance cannot be lower than 0'",
            "1.1, 'Draw trigger chance cannot be higher than 1'",})
    @DisplayName("Update competition - draw trigger chance only, invalid values - should return error code")
    void updateCompetition_invalidDrawTriggerChance_shouldReturnUpdated(double dtc, String validationErrorMsg) throws Exception {
        // arrange
        var simVals = new SimulationValues(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(simVals)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(null, null, dtc));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - invalid ID - should return error message")
    void updateCompetition_invalidId_shouldReturnUpdated() throws Exception {
        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(0.1, 0.2, 0.3));
        var response = mvc.perform(patch("/competition/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        assertEquals("Competition with ID [0] not found.", response);
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 0.2, 0.2, 'Variance cannot be lower than 0'",
            "1.1, 0.2, 0.2, 'Variance cannot be higher than 1'",
            "0.2, -1, 0.2, 'Score difference draw trigger cannot be lower than 0'",
            "0.2, 1.1, 0.2, 'Score difference draw trigger cannot be higher than 1'",
            "0.2, 0.2, -1, 'Draw trigger chance cannot be lower than 0'",
            "0.2, 0.2, 1.1, 'Draw trigger chance cannot be higher than 1'",
    })
    @DisplayName("Update competition - invalid simulation values  - should return error code")
    void updateCompetition_invalidSimValues_shouldReturnErrorCode(double variance,
                                                                  double sddt,
                                                                  double dtc,
                                                                  String validationErrorMsg) throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var dto = new UpdateCompetitionRequestDTO(null,
                null,
                null,
                new UpdateSimulationValuesDTO(variance, sddt, dtc));
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    @DisplayName("Search competitions - no criteria - should return all")
    void searchCompetitions_noCriteria_shouldReturnAll(int pageSize) throws Exception {
        // arrange
        competitionDataLayer.save(Competition.builder()
                .name("British League")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("Deutsche Liga")
                .country(CountryCode.DE)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("Spanish Liga")
                .country(CountryCode.GB)
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var response = mvc.perform(get("/competition/search")
                        .param("size", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var resultArray = new JSONObject(response);

        // assert
        assertEquals(pageSize, resultArray.getJSONArray("content").length());
    }

    @ParameterizedTest
    @CsvSource({
            "1,     '',   '',           '3'",
            "'',    GB,   '',           '2'",
            "'',    '',   2024/2025,    '3'",
            "'',    DE,   2024/2025,    '1'",
            "1,     '',   2024/2025,    '3'",
            "2,     IT,   2024/2025,    '0'",
            "2,     GB,   2025,         '1'",
    })
    @DisplayName("Search competitions - various criteria - should find expected amount")
    void searchCompetitions_variousCriteria_shouldFind(String name, String country, String season, int expected) throws Exception {
        // arrange
        competitionDataLayer.save(Competition.builder()
                .name("Brit League 1")
                .country(CountryCode.GB)
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("Brit League 2")
                .country(CountryCode.GB)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        competitionDataLayer.save(Competition.builder()
                .name("German League 1")
                .country(CountryCode.DE)
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("German League 2")
                .country(CountryCode.DE)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        competitionDataLayer.save(Competition.builder()
                .name("Italian League 1")
                .country(CountryCode.IT)
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("Italian League 2")
                .country(CountryCode.IT)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 10, 1))
                .simulationValues(new SimulationValues(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());

        // act
        var response = mvc.perform(get("/competition/search")
                        .param("name", name)
                        .param("country", country)
                        .param("season", season)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJson = new JSONObject(response);
        assertEquals(expected, responseJson.getJSONArray("content").length());
    }
}
