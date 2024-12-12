package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CompetitionControllerTests extends AbstractBaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FootballClubRepository fcrepository;
    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    @DisplayName("Should create a competition if there are clubs with given IDs in the database")
    void shouldCreateCompetitionFromExistingClubs() throws Exception {
        // arrange
        fcrepository.saveAll(List.of(
                FootballClub.builder().id(1L).name("Norf FC").victoryChance(0.3f).build(),
                FootballClub.builder().id(2L).name("Souf FC").victoryChance(0.4f).build(),
                FootballClub.builder().id(3L).name("West FC").victoryChance(0.5f).build(),
                FootballClub.builder().id(4L).name("East FC").victoryChance(0.6f).build()
        ));

        // act
        var mvcResult = mvc.perform(post("/competition").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompetitionRequestDTO(
                                List.of(1L, 2L, 3L, 4L),
                                null))))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        var compId = new JSONObject(mvcResult.getResponse()
                .getContentAsString()).getString("id");

        mvc.perform(get("/competition/" + -1))
                .andExpect(status().is4xxClientError());

        mvc.perform(get("/competition/" + compId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return all competitions")
    void shouldReturnAllCompetitions() throws Exception {
        // arrange
        competitionRepository.save(new Competition());
        competitionRepository.save(new Competition());

        // act
        var result = mvc.perform(get("/competition/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var resultArray = new JSONArray(result);

        // assert
        assertEquals(2, resultArray.length());
    }
}
