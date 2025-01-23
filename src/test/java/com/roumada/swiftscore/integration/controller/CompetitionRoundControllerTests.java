package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionRoundControllerTests extends AbstractBaseIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CompetitionRoundDataLayer competitionRoundDataLayer;

    @Test
    @DisplayName("Get competition round - valid ID - should return")
    void getCompetitionRound_validId_shouldReturn() throws Exception {
        // arrange
        CompetitionRound round = new CompetitionRound(1L, 1, Collections.emptyList());
        var roundId = competitionRoundDataLayer.save(round).getId();

        // act
        var result = mvc.perform(get("/competition/round/" + roundId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // assert
        var resultJSON = new JSONObject(result);
        assertEquals(roundId, resultJSON.getLong("id"));
    }

    @Test
    @DisplayName("Get competition round - invalid ID - should return error code")
    void getCompetitionRound_invalidId_shouldReturnError() throws Exception {
        mvc.perform(get("/competition/round/" + 111))
                .andExpect(status().is4xxClientError());
    }
}
