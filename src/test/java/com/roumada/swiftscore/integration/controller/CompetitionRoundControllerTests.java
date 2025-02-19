package com.roumada.swiftscore.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionRoundControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionRoundRepository repository;

    @Test
    @DisplayName("Get competition round - valid ID - should return")
    void getCompetitionRound_validId_shouldReturn() throws Exception {
        // arrange
        var roundId = repository.save(new CompetitionRound(1L, 1, Collections.emptyList())).getId();

        // act
        var response = mvc.perform(get("/competition/round/" + roundId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var round = objectMapper.readValue(response, CompetitionRound.class);
        assertThat(round.getId()).isEqualTo(roundId);
    }

    @Test
    @DisplayName("Get competition round - invalid ID - should return error code")
    void getCompetitionRound_invalidId_shouldReturnError() throws Exception {
        // arrange
        var id = 111;

        // act
        var response = mvc.perform(get("/competition/round/" + id))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        // assert
        JSONArray validationErrors = new JSONObject(response).getJSONArray("validationErrors");
        assertThat(validationErrors.length()).isEqualTo(1);
        assertThat(validationErrors.get(0))
                .isEqualTo("Competition round with ID [%s] not found.".formatted(id));
    }
}
