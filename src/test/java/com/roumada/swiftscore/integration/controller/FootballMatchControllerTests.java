package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class FootballMatchControllerTests extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FootballMatchRepository matchRepository;
    @Autowired
    private FootballClubRepository clubRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Get football match - valid ID - should return match")
    void getFootballMatch_validId_shouldReturn() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        clubRepository.save(fc1);
        clubRepository.save(fc2);
        var matchId = matchRepository.save(new FootballMatch(fc1, fc2)).getId();

        // act
        var response = mvc.perform(get("/match/" + matchId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // assert
        var match = objectMapper.readValue(response, FootballMatch.class);
        assertThat(match.getId()).isEqualTo(matchId);
    }

    @Test
    @DisplayName("Get football match - invalid ID - should return error code")
    void getFootballMatch_invalidId_shouldReturnErrorCode() throws Exception {
        // arrange
        var matchId = 999;

        // act
        var response = mvc.perform(get("/match/" + matchId))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("requestErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Couldn't find football match with ID [%s]".formatted(matchId));
    }
}
