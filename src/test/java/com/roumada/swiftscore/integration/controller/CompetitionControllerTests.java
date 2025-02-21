package com.roumada.swiftscore.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequest;
import com.roumada.swiftscore.model.dto.response.CompetitionResponse;
import com.roumada.swiftscore.model.dto.response.CompetitionSimulationResponse;
import com.roumada.swiftscore.model.dto.response.CompetitionSimulationSimpleResponse;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.persistence.datalayer.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.datalayer.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.datalayer.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.CompetitionTestUtils;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import io.micrometer.common.util.StringUtils;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionDataLayer competitionDataLayer;
    @Autowired
    private CompetitionRoundDataLayer competitionRoundDataLayer;
    @Autowired
    private FootballMatchDataLayer footballMatchDataLayer;

    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private FootballClubRepository footballClubRepository;

    @Test
    @DisplayName("Create competition - with valid football club IDs & football IDs only - should create")
    void createCompetition_validDataAndIdsOnly_isCreated() throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(clubs))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionResponse.class);
        assertThat(competitionRepository.findById(dto.id())).isPresent();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 8})
    @DisplayName("Create competition - with valid football club IDs & set participants - should create")
    void createCompetition_validIdsAndFillToParticipantsSet_isCreated(int participants) throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getTenFootballClubs()).stream().limit(4).toList();

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(participants, clubs))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionResponse.class);
        assertThat(competitionRepository.findById(dto.id())).isPresent();
    }

    @Test
    @DisplayName("Create competition - with uneven football club IDs amount & even participants - should create")
    void createCompetition_unevenFootballClubIdsButEvenFillToParticipants_isCreated() throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getTenFootballClubs()).stream().limit(3).toList();

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(8, clubs))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionResponse.class);
        assertThat(competitionRepository.findById(dto.id())).isPresent();
    }

    @ParameterizedTest
    @CsvSource({
            "7, 4",
            "4, 7"
    })
    @DisplayName("Create competition - with uneven total participant amount - should return error code")
    void createCompetition_unevenFootballClubIdsButUnevenFillToParticipants_isCreated(int clubsAmount, int participants) throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getTenFootballClubs()).stream().limit(clubsAmount).toList();

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(participants, clubs))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("The amount of clubs participating must be even.");
    }

    @Test
    @DisplayName("Create competition - with participants parameter only - should create")
    void createCompetition_withFillToParticipantsOnly_isCreated() throws Exception {
        // arrange
        FootballClubTestUtils.getIdsOfSavedClubs(footballClubRepository.saveAll(FootballClubTestUtils.getTenFootballClubs()));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(8))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionResponse.class);
        assertThat(competitionRepository.findById(dto.id())).isPresent();
    }

    @Test
    @DisplayName("Create competition - with uneven participants parameter only - should return error code")
    void createCompetition_withUnevenParticipantsOnly_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubRepository.saveAll(FootballClubTestUtils.getTenFootballClubs());

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(7))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("The amount of clubs participating must be even.");
    }

    @Test
    @DisplayName("Create competition - with invalid football club IDs - should return error code")
    void createCompetition_invalidIds_shouldReturnErrorCode() throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false))
                .stream().limit(2).toList();
        clubs.get(0).setId(-1L);

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(clubs))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Couldn't retrieve all clubs for given IDs and country.");
    }

    @Test
    @DisplayName("Create competition - not enough countries with given country - should return error code")
    void createCompetition_notEnoughClubsForCountry_shouldReturnErrorCode() throws Exception {
        // arrange
        var clubs = FootballClubTestUtils.getFourFootballClubs(false);
        clubs.get(0).setCountry(CountryCode.ES);
        footballClubRepository.saveAll(clubs);

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(clubs))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Couldn't retrieve all clubs for given IDs and country.");
    }

    @Test
    @DisplayName("Create competition  - with uneven football club ID count - should return error code")
    void createCompetition_invalidIdCount_shouldReturnErrorCode() throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false))
                .stream().limit(3).toList();

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(clubs))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("The amount of clubs participating must be even.");
    }

    @ParameterizedTest
    @CsvSource({
            "-0.01, 0, 0, 'Variance cannot be lower than 0'",
            "1.001, 0, 0, 'Variance cannot be greater than 1'",
            "0, -0.01, 0, 'Score difference draw trigger cannot be lower than 0'",
            "0, 1.001, 0, 'Score difference draw trigger cannot be greater than 1'",
            "0, 0, -0.01, 'Draw trigger chance cannot be lower than 0'",
            "0, 0, 1.001, 'Draw trigger chance cannot be greater than 1'",
    })
    @DisplayName("Create competition  - with invalid variance value - should return error code")
    void createCompetition_invalidVarianceNumber_shouldReturnErrorCode(double variance,
                                                                       double sddt,
                                                                       double dtc,
                                                                       String validationErrorMsg) throws Exception {
        // arrange
        var clubs = footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false));
        var simParameters = new SimulationParameters(variance, sddt, dtc);

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CompetitionTestUtils.getRequest(simParameters, clubs))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.get(0)).isEqualTo(validationErrorMsg);
    }

    @ParameterizedTest
    @CsvSource({
            "'2025-01-01', '',  'End date must be present'",
            "'', 2025-01-01, 'Start date must be present'",
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
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequest("",
                                CountryCode.GB,
                                startDate,
                                endDate,
                                new CompetitionParameters(0, ids, 0),
                                new SimulationParameters(0.0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.get(0)).isEqualTo(validationErrorMsg);
    }

    @Test
    @DisplayName("Create competition - with null name - should return error code")
    void createCompetition_withNullName_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequest(null,
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParameters(4, null, 0),
                                new SimulationParameters(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.get(0)).isEqualTo("Name cannot be null");
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    @DisplayName("Create competition - invalid relegation spots amount - should return error code")
    void createCompetition_invalidRelegationSpotsAmount_shouldReturnErrorCode(int relegationSpots) throws Exception {
        // arrange
        footballClubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false));

        // act
        var response = mvc.perform(post("/competition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCompetitionRequest("Competition",
                                CountryCode.GB,
                                "2025-01-01",
                                "2025-10-30",
                                new CompetitionParameters(4, null, relegationSpots),
                                new SimulationParameters(0)))))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.get(0))
                .isEqualTo("Amount of participants must be at least greater than two than relegation spots");
    }

    @Test
    @DisplayName("Delete competition - should return OK")
    void deleteCompetition_shouldReturnOK() throws Exception {
        // arrange
        var id = competitionDataLayer.save(Competition.builder().build()).getId();

        // act
        mvc.perform(delete("/competition/%s".formatted(id))).andExpect(status().isOk());

        // assert
        assertThat(competitionRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Get a competition - with valid ID - should return")
    void getCompetition_withValidID_shouldReturn() throws Exception {
        // arrange
        var competitionId = loadCompetitionWithFcs().getId();

        // act
        var response = mvc.perform(get("/competition/" + competitionId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionResponse.class);
        assertThat(dto.id()).isEqualTo(competitionId);
    }

    @Test
    @DisplayName("Get a competition - with invalid ID - should return error code")
    void getCompetition_withInvalidID_shouldReturnErrorCode() throws Exception {
        // act & assert
        mvc.perform(get("/competition/999")).andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3"})
    @DisplayName("Simulate competition  - can be simulated - should simulate set amount of times and return simulated round")
    void simulateCompetitionRound_canBeSimulated_shouldSimulateMultipleTimesAndReturn(String times) throws Exception {
        // arrange
        var competition = loadCompetitionWithFcs();

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(competition.getId()))
                        .param("times", times))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionSimulationResponse.class);
        assertThat(dto.simulatedUntil()).isEqualTo(Integer.valueOf(times));
        assertThat(dto.competitionId()).isEqualTo(competition.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3"})
    @DisplayName("Simulate competition  - can be simulated - should simulate set amount of times and return simulated rounds in simple form")
    void simulateCompetitionRound_canBeSimulated_shouldSimulateMultipleTimesAndReturnInSimpleForm(String times) throws Exception {
        // arrange
        var competition = loadCompetitionWithFcs();

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(competition.getId()))
                        .param("times", times)
                        .param("simplify", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, CompetitionSimulationSimpleResponse.class);
        assertThat(dto.simulatedUntil()).isEqualTo(Integer.valueOf(times));
        assertThat(dto.competitionId()).isEqualTo(competition.getId());
    }

    @Test
    @DisplayName("Simulate competition  - can be simulated - should return error code when no longer can")
    void simulateCompetitionRound_canBeSimulated_shouldReturnErrorCodeWhenNoLongerCan() throws Exception {
        // arrange
        var competition = loadCompetitionWithFcs();
        competition.setLastSimulatedRound(competition.getRounds().size());
        competitionRepository.save(competition);

        // act
        var response = mvc.perform(post("/competition/%s/simulate".formatted(competition.getId()))
                        .param("times", "1"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.get(0))
                .isEqualTo("Cannot simulate competition [%s] further.".formatted(competition.getId()));
    }

    @Test
    @DisplayName("Update competition - name only - should return updated")
    void updateCompetition_name_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(new SimulationParameters(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest("New Competition",
                null,
                null,
                null);

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(dto.name(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(saved
                        .getSimulationParameters()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(saved
                        .getSimulationParameters()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
        assertEquals(saved
                        .getSimulationParameters()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - country only - should return updated")
    void updateCompetition_country_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(new SimulationParameters(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                CountryCode.SE,
                null,
                null);

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(dto.country(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(saved
                        .getSimulationParameters()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(saved
                        .getSimulationParameters()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
        assertEquals(saved
                        .getSimulationParameters()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - simulation values only - should return updated")
    void updateCompetition_simParameters_shouldReturnUpdated() throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(new SimulationParameters(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(0.1, 0.2, 0.3));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(dto
                        .simulationParameters()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(dto
                        .simulationParameters()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
        assertEquals(dto
                        .simulationParameters()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @Test
    @DisplayName("Update competition - variance only - should return updated")
    void updateCompetition_variance_shouldReturnUpdated() throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(0.1, null, null));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(dto
                        .simulationParameters()
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(simParameters
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
        assertEquals(simParameters
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Variance cannot be lower than 0'",
            "1.1, 'Variance cannot be greater than 1'",})
    @DisplayName("Update competition - variance only, invalid values - should return error code")
    void updateCompetition_invalidVarianceOnly_shouldReturnErrorCode(double variance, String validationErrorMsg) throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(variance, null, null));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - score diff draw trigger only - should return updated")
    void updateCompetition_sddt_shouldReturnUpdated() throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(null, 0.2, null));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(simParameters
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(simParameters
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
        assertEquals(dto
                        .simulationParameters()
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Score difference draw trigger cannot be lower than 0'",
            "1.1, 'Score difference draw trigger cannot be greater than 1'",})
    @DisplayName("Update competition - score diff draw trigger only, invalid values - should return error code")
    void updateCompetition_invalidSddt_shouldReturnUpdated(double sddt, String validationErrorMsg) throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(null, sddt, null));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - draw trigger chance only - should return updated")
    void updateCompetition_dtc_shouldReturnUpdated() throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(null, null, 0.2));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJSON = new JSONObject(response);
        assertEquals(saved.getName(), responseJSON.getString("name"));
        assertEquals(saved.getCountry(), CountryCode.valueOf(responseJSON.getString("country")));
        assertEquals(simParameters
                        .variance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("variance"));
        assertEquals(dto
                        .simulationParameters()
                        .drawTriggerChance(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("drawTriggerChance"));
        assertEquals(simParameters
                        .scoreDifferenceDrawTrigger(),
                responseJSON
                        .getJSONObject("simulationParameters")
                        .getDouble("scoreDifferenceDrawTrigger"));
    }

    @ParameterizedTest
    @CsvSource({
            "-0.1, 'Draw trigger chance cannot be lower than 0'",
            "1.1, 'Draw trigger chance cannot be greater than 1'",})
    @DisplayName("Update competition - draw trigger chance only, invalid values - should return error code")
    void updateCompetition_invalidDrawTriggerChance_shouldReturnUpdated(double dtc, String validationErrorMsg) throws Exception {
        // arrange
        var simParameters = new SimulationParameters(0.6, 0.6, 0.6);
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(simParameters)
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(null, null, dtc));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Update competition - invalid ID - should return error message")
    void updateCompetition_invalidId_shouldReturnUpdated() throws Exception {
        // arrange
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(0.1, 0.2, 0.3));

        // act
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
            "1.1, 0.2, 0.2, 'Variance cannot be greater than 1'",
            "0.2, -1, 0.2, 'Score difference draw trigger cannot be lower than 0'",
            "0.2, 1.1, 0.2, 'Score difference draw trigger cannot be greater than 1'",
            "0.2, 0.2, -1, 'Draw trigger chance cannot be lower than 0'",
            "0.2, 0.2, 1.1, 'Draw trigger chance cannot be greater than 1'",
    })
    @DisplayName("Update competition - invalid simulation values  - should return error code")
    void updateCompetition_invalidsimParameters_shouldReturnErrorCode(double variance,
                                                                  double sddt,
                                                                  double dtc,
                                                                  String validationErrorMsg) throws Exception {
        // arrange
        var saved = competitionDataLayer.save(Competition.builder()
                .name("Competition")
                .country(CountryCode.GB)
                .simulationParameters(new SimulationParameters(0))
                .participants(Collections.emptyList())
                .rounds(Collections.emptyList())
                .build());
        var dto = new UpdateCompetitionRequest(null,
                null,
                null,
                new SimulationParameters(variance, sddt, dtc));

        // act
        var response = mvc.perform(patch("/competition/%s".formatted(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertTrue(validationErrorMsg.contains(validationErrors.get(0).toString()));
    }

    @Test
    @DisplayName("Search competitions - no criteria - should return all")
    void searchCompetitions_noCriteria_shouldReturnAll() throws Exception {
        // arrange
        loadCompetitionsWithFcs();

        // act
        var response = mvc.perform(get("/competition/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseJson = new JSONObject(response);
        assertThat(responseJson.getInt("totalPages")).isEqualTo(1);
        assertThat(responseJson.getInt("totalElements")).isEqualTo(6);
        List<CompetitionResponse> competitions = objectMapper.readValue(responseJson.getString("content"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CompetitionResponse.class));
        assertThat(competitions).hasSize(6);
    }

    @ParameterizedTest
    @CsvSource({
            "'azure',       '',             '',             3",
            "'',            'GB',           '',             2",
            "'',            '',             '2024/2025',    3",
            "'azure',       'GB',           '',             1",
            "'',            'GB',           '2024/2025',    1",
            "'emerald',     '',             '2025',         1",
            "'ruby',        'ES',           '2024/2025',    1",
    })
    @DisplayName("Search competitions - various criteria - should find expected amount")
    void searchCompetitions_variousCriteria_shouldFind(String name, String country, String season, int expected) throws Exception {
        // arrange
        loadCompetitionsWithFcs();

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
        List<CompetitionResponse> competitions = objectMapper.readValue(responseJson.getString("content"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CompetitionResponse.class));
        assertThat(competitions).hasSize(expected);
        for (CompetitionResponse c : competitions) {
            if (StringUtils.isNotEmpty(name)) {
                assertThat(c.name().toLowerCase()).contains(name.toLowerCase());
            }
            if (StringUtils.isNotEmpty(country)) {
                assertThat(c.country()).isEqualTo(CountryCode.valueOf(country));
            }
            if (StringUtils.isNotEmpty(season)) {
                assertThat(c.season()).isEqualTo(season);
            }
        }
    }
}
