package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class FootballMatchControllerTests extends AbstractBaseIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FootballMatchDataLayer dataLayer;

    @Test
    @DisplayName("Should retrieve football match")
    void shouldGetFootballMatch() throws Exception {
        // arrange
        var matchId = dataLayer.saveMatch(new FootballMatch(new FootballMatchStatistics(FootballClub.builder().id(1l).name("Norf FC").victoryChance(0.3f).build()),
                new FootballMatchStatistics(FootballClub.builder().id(1l).name("Norf FC").victoryChance(0.3f).build()))).getId();

        // act & assert
        mvc.perform(get("/match/" + matchId))
                .andExpect(status().isOk());
    }
}
