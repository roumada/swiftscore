package com.roumada.swiftscore.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import com.roumada.swiftscore.model.dto.request.CreateLeagueCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.persistence.repository.LeagueRepository;
import com.roumada.swiftscore.util.LeagueTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.neovisionaries.i18n.CountryCode.ES;
import static com.neovisionaries.i18n.CountryCode.GB;
import static com.roumada.swiftscore.util.LeagueTestUtils.getCreateLeagueCompetitionRequests;
import static com.roumada.swiftscore.util.LeagueTestUtils.getCreateLeagueRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LeagueControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    @Autowired
    private LeagueRepository repository;
    @Autowired
    private CompetitionRoundRepository competitionRoundRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Create league with two competitions - should create")
    void createLeagueWithTwoCompetitions_shouldCreate() throws Exception {
        // arrange
        loadFootballClubs();
        var request = getCreateLeagueRequest(4, 2);

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var league = objectMapper.readValue(response, League.class);
        assertThat(league.getId()).isNotNull();
        var season = league.getSeasons().get(0);
        assertThat(season.competitionIds()).doesNotContainNull();
    }

    @Test
    @DisplayName("Create league with two competitions - not enough unique clubs - should return error code")
    void createLeagueWithTwoCompetitions_notEnoughClubs_shouldReturnErrorCode() throws Exception {
        // arrange
        loadFootballClubs();
        var request = getCreateLeagueRequest(4, 4);

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var errors = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errors.requestErrors()).contains("Couldn't find enough clubs from given country to fill in the league.");
    }

    @Test
    @DisplayName("Create league with two competitions - unique participant IDs for competitions - should create")
    void createLeagueWithTwoCompetitions_uniqueParticipantIds_shouldCreate() throws Exception {
        // arrange
        loadFootballClubs();
        var participantIds = getFootballClubIdsForCountry(GB, 4);
        var request = new CreateLeagueRequest(
                "League",
                GB,
                "2020-08-01",
                "2021-06-01",
                List.of(new CreateLeagueCompetitionRequest(
                                "Competition 1",
                                new CompetitionParameters(4, participantIds.subList(0, 2), 0),
                                new SimulationParameters(0)),
                        new CreateLeagueCompetitionRequest(
                                "Competition 2",
                                new CompetitionParameters(2, participantIds.subList(2, 4), 0),
                                new SimulationParameters(0)
                        )));

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var league = objectMapper.readValue(response, League.class);
        assertThat(league.getId()).isNotNull();
        var season = league.getSeasons().get(0);
        assertThat(season.competitionIds()).doesNotContainNull();
    }

    @Test
    @DisplayName("Create league with two competitions - duplicated participantIds - should return error message")
    void createLeagueWithTwoCompetitions_duplicatedParticipantIds_shouldReturnErrorMessage() throws Exception {
        // arrange
        loadFootballClubs();
        var participantIds = List.of(getFootballClubIdsForCountry(GB, 1).get(0));
        var request = new CreateLeagueRequest(
                "League",
                GB,
                "2020-08-01",
                "2021-06-01",
                List.of(new CreateLeagueCompetitionRequest(
                                "Competition 1",
                                new CompetitionParameters(4, participantIds, 0),
                                new SimulationParameters(0)),
                        new CreateLeagueCompetitionRequest(
                                "Competition 2",
                                new CompetitionParameters(2, participantIds, 0),
                                new SimulationParameters(0)
                        )));

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var errors = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errors.requestErrors()).contains("Participant IDs for some of the competition requests are duplicated.");
    }

    @ParameterizedTest
    @CsvSource({
            "'',    GB, 'League name must not be empty'",
            "'Name',  , 'Country code must not be empty'",
    })
    @DisplayName("Create league with two competitions -  - should return error message")
    void createLeagueWithTwoCompetitions_invalidNonDateRequestVals_shouldReturnErrorMessage(String name,
                                                                                            CountryCode countryCode,
                                                                                            String errorMsg) throws Exception {
        // arrange
        var request = new CreateLeagueRequest(
                name,
                countryCode,
                "2020-01-01",
                "2020-02-01",
                getCreateLeagueCompetitionRequests(4, 6));

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var errors = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errors.requestErrors()).contains(errorMsg);
    }


    @ParameterizedTest
    @CsvSource({
            "'2025-01-01', '',  'End date cannot be empty'",
            "'', 2025-01-01, 'Start date cannot be empty'",
            "2025--01-01, '2025-02-02', 'Unparsable data format for one of the dates (must be YYYY-MM-DD)'",
            "'2025-01-01', '2025-01-02', 'Competition needs at least 6 days for a competition with 4 clubs.'",
            "'2025-02-01', '2025-01-01', 'Start date cannot be ahead of end date'",
            "'2020-01-01', '2025-01-01', 'The amount of days for a competition has exceed maximum duration [320]'"
    })
    @DisplayName("Create league with two competitions - invalid date request values - should return error message")
    void createLeagueWithTwoCompetitions_invalidDateRequestVals_shouldReturnErrorMessage(String startDate,
                                                                                         String endDate,
                                                                                         String errorMsg) throws Exception {
        // arrange
        loadFootballClubs();
        var request = new CreateLeagueRequest(
                "name",
                GB,
                startDate,
                endDate,
                getCreateLeagueCompetitionRequests(4, 6));

        // act
        var response = mvc.perform(post("/league")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var errors = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errors.requestErrors()).contains(errorMsg);
    }

    @Test
    @DisplayName("Find league by ID - should find")
    void findLeagueById_shouldFind() throws Exception {
        // arrange
        var leagueId = repository.save(new League("League", Collections.emptyList())).getId();

        // act
        var response = mvc.perform(get("/league/" + leagueId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var result = objectMapper.readValue(response, League.class);
        assertThat(result.getId()).isEqualTo(leagueId);
    }


    @Test
    @DisplayName("Find league by ID - invalid ID - should return error message")
    void findLeagueById_invalidId_shouldReturnErrorMessage() throws Exception {
        // arrange
        var invalidId = 999;

        // act
        var response = mvc.perform(get("/league/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var result = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(result.requestErrors()).contains("League with ID [%s] not found.".formatted(invalidId));
    }

    @Test
    @DisplayName("Delete league with ID - should return OK")
    void deleteLeagueWithId_shouldReturnOK() throws Exception {
        // arrange
        var leagueId = repository.save(new League("League", Collections.emptyList())).getId();

        // act
        mvc.perform(delete("/league/" + leagueId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // assert
        assertThat(repository.findById(leagueId)).isEmpty();
    }

    @Test
    @DisplayName("Simulate competitions within league - simulate once - should simulate properly")
    void simulateLeague_once_shouldSimulate() throws Exception {
        // arrange
        loadCompetitionsWithFcs();
        var comps = getCompetitionsForCountry(GB, 2);
        var compIds = comps.stream().map(Competition::getId).toList();
        long id = repository.save(LeagueTestUtils.getForCompetitions(comps)).getId();

        // act
        mvc.perform(post("/league/%s/simulate".formatted(id))
                        .queryParam("times", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // assert
        comps = competitionRepository.findAllById(compIds);
        assertThat(comps).isNotEmpty();
        for (Competition c : comps) {
            assertThat(c.getLastSimulatedRound()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Simulate competitions within league - simulate enough times to simulate one league until end - should simulate properly")
    void simulateLeague_simulateOneCompetitionUntilEnd_shouldSimulate() throws Exception {
        // arrange
        loadCompetitionsWithFcs();
        var comps = getCompetitionsForCountry(GB, 2);
        var compIds = comps.stream().map(Competition::getId).toList();
        var times = Math.min(comps.get(0).getParticipants().size(), comps.get(1).getParticipants().size()) * 2 - 1;
        long id = repository.save(LeagueTestUtils.getForCompetitions(comps)).getId();

        // act
        mvc.perform(post("/league/%s/simulate".formatted(id))
                        .queryParam("times", String.valueOf(times))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // assert
        comps = competitionRepository.findAllById(compIds);
        assertThat(comps).isNotEmpty();
        var timesSimulated = comps.stream().map(Competition::getLastSimulatedRound).toList();
        assertThat(timesSimulated).contains(times).contains(times - 1);
        assertThat(comps.get(0).isFullySimulated() || comps.get(1).isFullySimulated()).isTrue();
    }

    @Test
    @DisplayName("Simulate competitions within league - simulate enough times to simulate oll until end - should simulate properly")
    void simulateLeague_simulateAllUntilEnd_shouldSimulate() throws Exception {
        // arrange
        loadCompetitionsWithFcs();
        var comps = getCompetitionsForCountry(GB, 2);
        var compIds = comps.stream().map(Competition::getId).toList();
        long id = repository.save(LeagueTestUtils.getForCompetitions(comps)).getId();

        // act
        mvc.perform(post("/league/%s/simulate".formatted(id))
                        .queryParam("times", String.valueOf(20))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // assert
        comps = competitionRepository.findAllById(compIds);
        assertThat(comps).isNotEmpty();
        assertThat(comps.get(0).isFullySimulated()).isTrue();
        assertThat(comps.get(1).isFullySimulated()).isTrue();
    }

    @Test
    @DisplayName("Simulate competitions within league - invalid ID - should return error code")
    void simulateLeague_invalidId_shouldReturnErrorCode() throws Exception {
        // arrange
        var invalidId = 999;

        // act
        var response = mvc.perform(post("/league/%s/simulate".formatted(invalidId))
                        .queryParam("times", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var result = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(result.requestErrors()).contains("League with ID [%s] not found.".formatted(invalidId));
    }

    @Test
    @DisplayName("Start new league season - should start new season")
    void startNewLeagueSeason_shouldStart() throws Exception {
        // arrange
        loadCompetitionsWithFcs();
        // NOTE: loading one competition from different countries to ensure club ID uniqueness
        var gbComp = getCompetitionsForCountry(GB, 1).get(0);
        var esComp = getCompetitionsForCountry(ES, 1).get(0);
        gbComp.setLastSimulatedRound(gbComp.getRounds().size());
        gbComp.setRelegationSpots(1);
        esComp.setLastSimulatedRound(esComp.getRounds().size());
        esComp.setRelegationSpots(0);
        competitionRepository.save(gbComp);
        competitionRepository.save(esComp);
        var leagueId = repository.save(LeagueTestUtils.getForCompetitions(List.of(gbComp, esComp))).getId();

        // act
         var response = mvc.perform(post("/league/%s/advance".formatted(leagueId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, League.class);
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getSeasons()).isNotNull();
    }

    @Test
    @DisplayName("Start new league season - one of the leagues is not yet concluded - should return error code")
    void startNewLeagueSeason_oneLeagueNotConcluded_shouldReturnErrorCode() throws Exception {
        // arrange
        loadCompetitionsWithFcs();
        // NOTE: loading one competition from different countries to ensure club ID uniqueness
        var gbComp = getCompetitionsForCountry(GB, 1).get(0);
        var esComp = getCompetitionsForCountry(ES, 1).get(0);
        gbComp.setLastSimulatedRound(gbComp.getRounds().size());
        gbComp.setRelegationSpots(1);
        esComp.setLastSimulatedRound(1);
        esComp.setRelegationSpots(0);
        competitionRepository.save(gbComp);
        competitionRepository.save(esComp);
        var leagueId = repository.save(LeagueTestUtils.getForCompetitions(List.of(gbComp, esComp))).getId();

        // act
        var response = mvc.perform(post("/league/%s/advance".formatted(leagueId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        var dto = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(dto.requestErrors()).hasSize(1);
        assertThat(dto.requestErrors().get(0)).isEqualTo("League with ID [%s] cannot be yet advanced.".formatted(leagueId));
    }
}
