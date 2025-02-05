package com.roumada.swiftscore.e2e;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatisticsE2ETests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository clubRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Should correctly generate statistics for a competition")
    void shouldCorrectlyGenerateStatisticsForCompetition() throws JSONException {
        // arrange
        var clubIds = clubRepository.saveAll(FootballClubTestUtils.getFourFootballClubs(false))
                .stream().map(FootballClub::getId).toList();
        CompetitionRequestDTO request = new CompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2025-01-01",
                "2025-10-30",
                clubIds,
                null,
                new SimulationValues(0));

        // STEP 1: create competition
        Response response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/competition")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        var compId = response.jsonPath().getInt("id");

        // STEP 2: simulate competition until completion
        for (int i = 0; i < 6; i++) {
            given()
                    .port(port)
                    .param("times", "1")
                    .when()
                    .post("/competition/%s/simulate".formatted(compId))
                    .then()
                    .statusCode(200);
        }

        // STEP 3: retrieve statistics
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/competition/%s".formatted(compId))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        JSONArray statisticsArray = new JSONArray(response.asString());
        assertEquals(4, statisticsArray.length());
        for (int i = 0; i < statisticsArray.length(); i++) {
            JSONObject statisticsJson = statisticsArray.getJSONObject(i);
            assertTrue(statisticsJson.getInt("wins") + statisticsJson.getInt("draws") + statisticsJson.getInt("losses") > 0);
            assertTrue(statisticsJson.getInt("goalsScored") + statisticsJson.getInt("goalsScored") > 0);
            assertNotNull(statisticsJson.getJSONArray("lastMatchesStatistics"));
            for (int j = i; j < statisticsArray.length(); j++) {
                JSONObject statisticsJsonNext = statisticsArray.getJSONObject(j);
                assertTrue(statisticsJson.getInt("points") >= statisticsJsonNext.getInt("points"));
                assertTrue(statisticsJson.getInt("wins") + statisticsJson.getInt("draws") >=
                        statisticsJsonNext.getInt("wins") + statisticsJsonNext.getInt("draws"));
            }
        }
    }

    @Test
    @DisplayName("Should either include on exclude unresolved matches depending on query")
    void shouldIncludeOrExcludeUnresolvedMatches() throws JSONException {
        // arrange
        var fc1 = FootballClubTestUtils.getTwoFootballClubs().get(0);
        var clubIds
                = clubRepository.saveAll(FootballClubTestUtils.getTwoFootballClubs()).stream().map(FootballClub::getId).toList();
        CompetitionRequestDTO request = new CompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2025-01-01",
                "2025-10-30",
                clubIds,
                null,
                new SimulationValues(0));

        // STEP 1: create first competition
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/competition")
                .then()
                .statusCode(200)
                .extract()
                .response();
        var firstCompId = response.jsonPath().getInt("id");

        // STEP 2: search for statistics without unresolved matches - return none
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        JSONObject responseJson = new JSONObject(response.asString());
        assertEquals(fc1.getName(), responseJson.getJSONObject("footballClub").getString("name"));
        assertEquals(fc1.getStadiumName(), responseJson.getJSONObject("footballClub").getString("stadiumName"));
        assertEquals(fc1.getVictoryChance(), responseJson.getJSONObject("footballClub").getDouble("victoryChance"));
        assertEquals(0, responseJson.getJSONArray("statistics").length());

        // STEP 3: search for statistics with unresolved matches - return two from first competition
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s?includeUnresolved=true".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        responseJson = new JSONObject(response.asString());
        assertEquals(2, responseJson.getJSONArray("statistics").length());

        // STEP 4: create second competition
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/competition")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        var secondCompId = response.jsonPath().getInt("id");

        // STEP 5: simulate comp twice to resolve its matches
        given()
                .port(port)
                .param("times", "2")
                .when()
                .post("/competition/%s/simulate".formatted(secondCompId))
                .then()
                .statusCode(200)
                .extract()
                .response();

        // STEP 6: search for statistics
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        responseJson = new JSONObject(response.asString());
        assertEquals(2, responseJson.getJSONArray("statistics").length());

        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s?includeUnresolved=true".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        responseJson = new JSONObject(response.asString());
        assertEquals(4, responseJson.getJSONArray("statistics").length());

        // STEP 7: delete first competition
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .delete("/competition/%s".formatted(firstCompId))
                .then()
                .statusCode(200);

        // STEP 8: search for statistics
        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        responseJson = new JSONObject(response.asString());
        assertEquals(2, responseJson.getJSONArray("statistics").length());

        response =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .get("/statistics/club/%s?includeUnresolved=true".formatted(clubIds.get(0)))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        responseJson = new JSONObject(response.asString());
        assertEquals(2, responseJson.getJSONArray("statistics").length());
    }
}
