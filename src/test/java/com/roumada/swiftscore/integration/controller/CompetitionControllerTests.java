package com.roumada.swiftscore.integration.controller;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.CompetitionUpdateRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var mvcResult = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-01",
                                        ids,
                                        null,
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
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CompetitionRequestDTO("",
                                        CountryCode.GB,
                                        "2025-01-01",
                                        "2025-10-30",
                                        List.of(1L, 2L, 3L, 9L),
                                        null,
                                        new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        assertEquals("Failed to generate competition - failed to retrieve enough clubs from the database.", errorMsg);
    }

    @Test
    @DisplayName("Create competition  - with uneven football club ID count - should return error code")
    void createCompetition_invalidIdCount_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var errorMsg = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                List.of(1L, 2L, 3L),
                                null,
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
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                ids,
                                null,
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
    @ValueSource(doubles = {-0.1, 1.1}) // One parameter: int
    @DisplayName("Create competition  - with invalid draw trigger chance value - should return error code")
    void createCompetition_invalidDrawTriggerChanceValue_shouldReturnErrorCode(double drawTriggerChance) throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-11-10",
                                ids,
                                null,
                                new SimulationValues(0.0, 0.0, drawTriggerChance)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        if (drawTriggerChance < 0) {
            assertEquals("Draw trigger cannot be lower than 0",
                    validationErrors.get(0));
        } else {
            assertEquals("Draw trigger cannot be higher than 1",
                    validationErrors.get(0));
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1}) // One parameter: int
    @DisplayName("Create competition  - with invalid score diff draw trigger value - should return error code")
    void createCompetition_invalidScoreDiffDrawTriggerValue_shouldReturnErrorCode(double scoreDifferenceDrawTrigger) throws Exception {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                ids,
                                null,
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
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO("",
                                CountryCode.GB,
                                startDate,
                                endDate,
                                ids,
                                null,
                                new SimulationValues(0.0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Create competition  - with null name - should return error code")
    void createCompetition_withNullName_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO(null,
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                List.of(1L, 2L, 3L),
                                null,
                                new SimulationValues(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertEquals("Name cannot be null", validationErrors.get(0));
    }

    @Test
    @DisplayName("Delete competition - exists - should return OK")
    void deleteCompetition_exists_shouldReturnOK() throws Exception {
        // arrange
        var id = competitionDataLayer.save(Competition.builder().build()).getId();

        // act
        mvc.perform(delete("/competition/%s".formatted(id))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete competition - doesn't exist - should return no content")
    void deleteCompetition_nonExistent_shouldReturnNoContent() throws Exception {
        // act
        mvc.perform(delete("/competition/-1")).andExpect(status().isNoContent());
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
    @DisplayName("Get all competitions - should return")
    void getAllCompetitions_shouldReturnAll() throws Exception {
        // arrange
        var round1 = new CompetitionRound(1, Collections.emptyList());
        var round2 = new CompetitionRound(1, Collections.emptyList());
        competitionRoundDataLayer.save(round1);
        competitionRoundDataLayer.save(round2);
        var savedClubs = footballClubDataLayer.saveAll(FootballClubTestUtils.getFourFootballClubs(false));
        competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .simulationValues(new SimulationValues(0))
                .participants(savedClubs)
                .rounds(List.of(round1))
                .build());
        competitionDataLayer.save(Competition.builder()
                .name("Competition")
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
                .simulationValues(new SimulationValues(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round))
                .build());

        // act
        mvc.perform(post("/competition/%s/simulate".formatted(saved.getId())).param("times", "1")).andExpect(status().isOk());
        mvc.perform(post("/competition/%s/simulate".formatted(saved.getId()))).andExpect(status().is4xxClientError());
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
        var dto = new CompetitionUpdateRequestDTO("New Competition",
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
        var dto = new CompetitionUpdateRequestDTO(null,
                CountryCode.SE,
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
        var dto = new CompetitionUpdateRequestDTO(null,
                null,
                new SimulationValues(0.1, 0.2, 0.3));
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
    @DisplayName("Update competition - invalid ID - should return error message")
    void updateCompetition_invalidId_shouldReturnUpdated() throws Exception {
        // act
        var dto = new CompetitionUpdateRequestDTO(null,
                null,
                new SimulationValues(0.1, 0.2, 0.3));
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
            "0.2, 0.2, -1, 'Draw trigger cannot be lower than 0'",
            "0.2, 0.2, 1.1, 'Draw trigger cannot be higher than 1'",
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
        var dto = new CompetitionUpdateRequestDTO(null,
                null,
                new SimulationValues(variance, sddt, dtc));
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
}
