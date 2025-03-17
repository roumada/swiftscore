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
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.persistence.repository.LeagueRepository;
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
        var participantIds = getFootballClubIdsForCountry(CountryCode.GB, 4);
        var request = new CreateLeagueRequest(
                "League",
                CountryCode.GB,
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
        var participantIds = List.of(getFootballClubIdsForCountry(CountryCode.GB, 1).get(0));
        var request = new CreateLeagueRequest(
                "League",
                CountryCode.GB,
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
            "'2025-01-01', '',  'End date must be present'",
            "'', 2025-01-01, 'Start date must be present'",
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
                CountryCode.GB,
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
        assertThat(result.requestErrors()).contains("Competition with ID [%s] not found.".formatted(invalidId));
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
}
