package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        var dto = new FootballClubDTO("FC1", 0.5f);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create football club - with invalid victory chance - should return error code")
    void createFootballClub_invalidVictoryChance_shouldReturnErrorCode() throws Exception {
        // arrange
        var dto = new FootballClubDTO("FC1", 2.2f);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create football club - with missing name - should return error code")
    void createFootballClub_missingName_shouldReturnErrorCode() throws Exception {
        // arrange
        var dto = new FootballClubDTO(null, 2.2f);
        // act & assert
        mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get football club - with valid ID - should return")
    void getFootballClub_validId_shouldReturn() throws Exception {
        // arrange
        var fcID = footballClubDataLayer.save(new FootballClub("FC1", 0.5f)).getId();

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
        footballClubDataLayer.save(new FootballClub("FC1", 0.5f));

        // act & assert
        mvc.perform(get("/footballclub/" + 999))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get all football clubs  - should return")
    void getAllFootballClubs_shouldReturn() throws Exception {
        // arrange
        footballClubDataLayer.save(new FootballClub("FC1", 0.5f));
        footballClubDataLayer.save(new FootballClub("FC2", 0.4f));

        // act
        var response = mvc.perform(get("/footballclub/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        var responseJSON = new JSONArray(response);

        // assert
        assertEquals(2, responseJSON.length());
    }
}
