package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.data.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
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
    private FootballMatchDataLayer fmdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Should retrieve football match")
    void shouldGetFootballMatch() throws Exception {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcdl.save(fc1);
        fcdl.save(fc2);
        var stats1 = new FootballMatchStatistics(fc1);
        var stats2 = new FootballMatchStatistics(fc2);
        var matchId = fmdl.saveMatch(new FootballMatch(stats1, stats2)).getId();

        // act & assert
        mvc.perform(get("/match/" + matchId))
                .andExpect(status().isOk());
    }
}
