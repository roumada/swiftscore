package com.roumada.swiftscore.integration.controller;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class FootballClubControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FootballClubDataLayer footballClubDataLayer;

    @Test
    @DisplayName("Create football club - with valid data - should save")
    void createFootballClub_validData_shouldSave() throws Exception {
        // arrange
        var dto = new CreateFootballClubRequestDTO("FC1", CountryCode.GB, "", 0.5f);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Create football club - with invalid victory chance - should return error code")
    void createFootballClub_invalidVictoryChance_shouldReturnErrorCode(double victoryChance) throws Exception {
        // arrange
        var dto = new CreateFootballClubRequestDTO("FC1", CountryCode.GB, "", victoryChance);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create football club - with null name - should return error code")
    void createFootballClub_nullName_shouldReturnErrorCode() throws Exception {
        // arrange
        var dto = new CreateFootballClubRequestDTO(null, CountryCode.GB, "", 0.5);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create football club - with null country code - should return error code")
    void createFootballClub_nullCountryCode_shouldReturnErrorCode() throws Exception {
        // arrange
        var dto = new CreateFootballClubRequestDTO("", null, "", 0.5);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create football club - with null stadium name - should return error code")
    void createFootballClub_nullStadiumName_shouldReturnErrorCode() throws Exception {
        // arrange
        var dto = new CreateFootballClubRequestDTO("", CountryCode.GB, null, 0.5);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get football club - with valid ID - should return")
    void getFootballClub_validId_shouldReturn() throws Exception {
        // arrange
        var fcID = footballClubDataLayer.save(FootballClub.builder().name("FC1").victoryChance(0.5).build()).getId();

        // act
        var response = mvc.perform(get("/footballclub/" + fcID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        assertEquals(fcID, new JSONObject(response).getLong("id"));
    }

    @Test
    @DisplayName("Get football club - with invalid ID - should return error code")
    void getFootballClub_invalidId_shouldReturnErrorCode() throws Exception {
        // arrange
        footballClubDataLayer.save(FootballClub.builder().name("FC1").victoryChance(0.5).build());

        // act & assert
        mvc.perform(get("/footballclub/" + 999))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Patch football club - change name -  should return patched")
    void patchFC_name_shouldReturn() throws Exception {
        // arrange
        var fc = footballClubDataLayer.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequestDTO(
                "FC2",
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
        var responseFC = new JSONObject(response);

        // assert
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
        var fc = footballClubDataLayer.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequestDTO(
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
        var responseFC = new JSONObject(response);

        // assert
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
        var fc = footballClubDataLayer.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequestDTO(
                null,
                null,
                "FC Park",
                0.0);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseFC = new JSONObject(response);

        // assert
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
        var fc = footballClubDataLayer.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequestDTO(
                null,
                null,
                null,
                0.7);

        // act
        var response = mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseFC = new JSONObject(response);

        // assert
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
        var fc = footballClubDataLayer.save(FootballClubTestUtils.getClub(false));
        var dto = new CreateFootballClubRequestDTO(
                null,
                null,
                null, victoryChance);

        // act
        mvc.perform(patch("/footballclub/%s".formatted(fc.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @CsvSource({
            "'1', AN, '', '1', '1'",
            "'', AN, '', '11', '10'",
            "'1', AN, '', '3', '2'",
            "'', GB, '', '1', '1'",
            "'', GB, '', '8', '4'",
            "'', AN, '1', '1', '1'",
            "'', AN, '1', '3', '2'",
            "'1', GB, '', '1', '1'",
            "'', GB, '2', '1', '1'",
            "'1', AN, '1', '1', '1'",
            "'9', GB, '9', '1', '1'",
    })
    @DisplayName("Search football clubs - various criteria - should return expected amount")
    void searchFootballClubs_variousCriteria_shouldReturnExpectedAmount(String name, CountryCode country, String stadiumName, int pageSize, int expected) throws Exception {
        // arrange
        footballClubDataLayer.saveAll(FootballClubTestUtils.getTenFootballClubsWithVariousCountries());

        // act
        var response = mvc.perform(get("/footballclub/search")
                        .param("size", String.valueOf(pageSize))
                        .param("name", name)
                        .param("country", country == CountryCode.AN ? null : country.toString())
                        .param("stadiumName", stadiumName)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONObject(response);

        // assert
        assertEquals(expected, responseJSON.getJSONArray("content").length());
    }
}
