package com.roumada.swiftscore.integration.controller;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class FootballClubControllerTest extends AbstractBaseIntegrationTest {

    @Autowired
    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenFootballClub_whenSave_thenGetFootballClub() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/footballclub").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FootballClub.builder().name("Norf FC").victoryChance(0.3f).build())))
                .andExpect(status().isOk())
                .andReturn();

        String productId = mvcResult.getResponse()
                .getContentAsString();

        mvc.perform(get("/footballclub/" + productId))
                .andExpect(status().isOk());
    }
}
