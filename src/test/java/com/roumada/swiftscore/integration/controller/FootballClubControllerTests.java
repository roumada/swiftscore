package com.roumada.swiftscore.integration.controller;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class FootballClubControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FootballClubRepository repository;

    @Test
    @DisplayName("Create football club - with valid values - should save")
    void createFootballClub_validValues_shouldSave() throws Exception {
        // arrange
        var dto = new CreateFootballClubRequest("FC1", CountryCode.GB, "A", 0.5f);

        // act
        var response = mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var club = objectMapper.readValue(response, FootballClub.class);
        assertThat(club.getId()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            ", 'GB', 'stadiumName', 0.5,          'Name cannot be null'",
            "'name', , 'stadiumName', 0.5,          'Country cannot be null'",
            "'name', GB, , 0.5,                   'Stadium name cannot be empty'",
            "'name', GB, 'stadiumName', -0.01,      'Victory chance cannot be lower than 0'",
            "'name', GB, 'stadiumName', 1.001,      'Victory chance cannot be greater than 1'",
    })
    @DisplayName("Create football club - with invalid values - should return error message")
    void createFootballClub_invalidValues_shouldReturnErrorMessage(String name,
                                                                   CountryCode country,
                                                                   String stadiumName,
                                                                   double victoryChance,
                                                                   String validationErrorMsg) throws Exception {
        // arrange
        var dto = new CreateFootballClubRequest(name, country, stadiumName, victoryChance);

        // act
        var response = mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo(validationErrorMsg);
    }

    @Test
    @DisplayName("Get football club - with valid ID - should return")
    void getFootballClub_validId_shouldReturn() throws Exception {
        // arrange
        var fcID = repository.save(FootballClub.builder().name("FC1").victoryChance(0.5).build()).getId();

        // act
        var response = mvc.perform(get("/footballclub/" + fcID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var club = objectMapper.readValue(response, FootballClub.class);
        assertThat(club.getId()).isEqualTo(fcID);
    }

    @Test
    @DisplayName("Get football club - with invalid ID - should return error code")
    void getFootballClub_invalidId_shouldReturnErrorCode() throws Exception {
        // arrange
        var fcId = 999;

        // act
        var response = mvc.perform(get("/footballclub/" + fcId))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Unable to find football club with given id [%s]".formatted(fcId));
    }

    @Test
    @DisplayName("Patch football club - change name -  should return patched")
    void patchFC_name_shouldReturn() throws Exception {
        // arrange
        var fc = repository.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequest(
                "AAAABBB",
                null,
                null,
                0.0);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseFC = new JSONObject(response);
        assertEquals(fc.getId(), responseFC.getLong("id"));
        assertEquals(dto.name(), responseFC.getString("name"));
        assertEquals(fc.getCountry(), CountryCode.valueOf(responseFC.getString("country")));
        assertEquals(fc.getStadiumName(), responseFC.getString("stadiumName"));
        assertEquals(fc.getVictoryChance(), responseFC.getDouble("victoryChance"));
    }

    @Test
    @DisplayName("Patch football club - change country -  should return patched")
    void patchFC_country_shouldReturn() throws Exception {
        // arrange
        var fc = repository.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequest(
                null,
                CountryCode.PL,
                null,
                0.0);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseFC = new JSONObject(response);
        assertEquals(fc.getId(), responseFC.getLong("id"));
        assertEquals(fc.getName(), responseFC.getString("name"));
        assertEquals(dto.country(), CountryCode.valueOf(responseFC.getString("country")));
        assertEquals(fc.getStadiumName(), responseFC.getString("stadiumName"));
        assertEquals(fc.getVictoryChance(), responseFC.getDouble("victoryChance"));
    }

    @Test
    @DisplayName("Patch football club - change stadium name - should return patched")
    void patchFC_stadiumName_shouldReturn() throws Exception {
        // arrange
        var fc = repository.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequest(
                null,
                null,
                "BBBBCCCC",
                0.0);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseFC = new JSONObject(response);
        assertEquals(fc.getId(), responseFC.getLong("id"));
        assertEquals(fc.getName(), responseFC.getString("name"));
        assertEquals(fc.getCountry(), CountryCode.valueOf(responseFC.getString("country")));
        assertEquals(dto.stadiumName(), responseFC.getString("stadiumName"));
        assertEquals(fc.getVictoryChance(), responseFC.getDouble("victoryChance"));
    }

    @Test
    @DisplayName("Patch football club - change victory chance - should return patched")
    void patchFC_victoryChance_shouldReturn() throws Exception {
        // arrange
        var fc = repository.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequest(
                null,
                null,
                null,
                0.9181);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var responseFC = new JSONObject(response);
        assertEquals(fc.getId(), responseFC.getLong("id"));
        assertEquals(fc.getName(), responseFC.getString("name"));
        assertEquals(fc.getCountry(), CountryCode.valueOf(responseFC.getString("country")));
        assertEquals(fc.getStadiumName(), responseFC.getString("stadiumName"));
        assertEquals(dto.victoryChance(), responseFC.getDouble("victoryChance"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Patch football club - with invalid victory chance - should return error")
    void patchFC_invalidVictoryChance_shouldReturnError(double victoryChance) throws Exception {
        // arrange
        var fc = repository.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequest(
                null,
                null,
                null, victoryChance);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Victory chance cannot be lower than 0 or higher than 1");
    }

    @ParameterizedTest
    @CsvSource({
            "'1', '', '', '1', '1'",
            "'', '', '', '11', '10'",
            "'1', '', '', '3', '2'",
            "'', GB, '', '1', '1'",
            "'', GB, '', '8', '4'",
            "'', '', '1', '1', '1'",
            "'', '', '1', '3', '2'",
            "'1', GB, '', '1', '1'",
            "'', GB, '2', '1', '1'",
            "'1', '', '1', '1', '1'",
            "'9', GB, '9', '1', '1'",
    })
    @DisplayName("Search football clubs - various criteria - should return expected amount")
    void searchFootballClubs_variousCriteria_shouldReturnExpectedAmount(String name,
                                                                        String country,
                                                                        String stadiumName,
                                                                        int pageSize,
                                                                        int expected) throws Exception {
        // arrange
        repository.saveAll(FootballClubTestUtils.getTenFootballClubsWithVariousCountries());

        // act
        var response = mvc.perform(get("/footballclub/search")
                        .param("size", String.valueOf(pageSize))
                        .param("name", name)
                        .param("country", country)
                        .param("stadiumName", stadiumName)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();


        // assert
        var responseJson = new JSONObject(response);
        List<FootballClub> competitions = objectMapper.readValue(responseJson.getString("content"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FootballClub.class));
        assertThat(competitions).hasSize(expected);
    }
}
