package com.roumada.swiftscore.e2e;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateSimulationValuesDTO;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompetitionE2ETests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository clubRepository;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void init() {
        RestAssured.basePath = "/competition";
    }

    @Test
    @DisplayName("Should create, update and then delete competition")
    void shouldCreateUpdateAndDeleteCompetition() {
        // arrange
        var clubIds = clubRepository.saveAll(FootballClubTestUtils.getTwoFootballClubs())
                .stream().map(FootballClub::getId).toList();
        CreateCompetitionRequestDTO request = new CreateCompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2025-01-01",
                "2025-10-30",
                clubIds,
                null,
                new SimulationValues(0));

        // STEP 1: create competition
        Response createCompetitionResponse =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("")
                        .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("lastSimulatedRound", equalTo(0))
                        .body("name", equalTo(request.name()))
                        .body("startDate", equalTo(request.startDate()))
                        .body("endDate", equalTo(request.endDate()))
                        .body("season", equalTo("2025"))
                        .body("country", equalTo(request.country().toString()))
                        .body("simulationValues.variance", equalTo(0.0F))
                        .body("simulationValues.scoreDifferenceDrawTrigger", equalTo(0.0F))
                        .body("simulationValues.drawTriggerChance", equalTo(0.0F))
                        .extract()
                        .response();

        int id = createCompetitionResponse.jsonPath().getInt("id");

        // STEP 2: update competition
        UpdateCompetitionRequestDTO updateRequest
                = new UpdateCompetitionRequestDTO(
                "New Competition",
                CountryCode.AN,
                new UpdateSimulationValuesDTO(0.1, 0.2, 0.3));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/%s".formatted(id))
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(updateRequest.name()))
                .body("country", equalTo(updateRequest.country().toString()))
                .body("simulationValues.variance", equalTo(0.1F))
                .body("simulationValues.scoreDifferenceDrawTrigger", equalTo(0.2F))
                .body("simulationValues.drawTriggerChance", equalTo(0.3F))
                .extract()
                .response();

        // STEP 3: delete competition
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/%s".formatted(id))
                .then()
                .statusCode(200);

        // STEP 4: attempt to delete non-existent competition and receive 204 code
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/%s".formatted(id))
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should create and simulate competition until can no longer be simulated and delete competition and its matches")
    void shouldCreateSimulateAndDeleteCompetition() throws JSONException {
        // arrange
        var clubIds = clubRepository.saveAll(FootballClubTestUtils.getTwoFootballClubs())
                .stream().map(FootballClub::getId).toList();
        CreateCompetitionRequestDTO request = new CreateCompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2025-01-01",
                "2025-10-30",
                clubIds,
                null,
                new SimulationValues(0));

        // STEP 1: create competition
        Response createCompetitionResponse =
                given()
                        .port(port)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("")
                        .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("lastSimulatedRound", equalTo(0))
                        .body("name", equalTo(request.name()))
                        .body("startDate", equalTo(request.startDate()))
                        .body("endDate", equalTo(request.endDate()))
                        .body("country", equalTo(request.country().toString()))
                        .body("simulationValues.variance", equalTo(0.0F))
                        .body("simulationValues.scoreDifferenceDrawTrigger", equalTo(0.0F))
                        .body("simulationValues.drawTriggerChance", equalTo(0.0F))
                        .extract()
                        .response();

        int compId = createCompetitionResponse.jsonPath().getInt("id");

        // STEP 2: simulate competition
        Response simulateCompResponse =
                given()
                        .port(port)
                        .param("times", "1")
                        .when()
                        .post("/%s/simulate".formatted(compId))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //// check simulated match
        var jsonResponse = new JSONObject(simulateCompResponse.asString());
        assertEquals(1, jsonResponse.getJSONArray("rounds").getJSONObject(0).getInt("round"));
        assertEquals(1, jsonResponse.getJSONArray("rounds").getJSONObject(0).getJSONArray("matches").length());

        JSONObject fm1Json = (JSONObject) jsonResponse.getJSONArray("rounds").getJSONObject(0).getJSONArray("matches").get(0);
        assertEquals(compId, fm1Json.getLong("competitionId"));
        assertNotSame(fm1Json.get("matchResult").toString(), FootballMatch.MatchResult.UNFINISHED.toString());


        // STEP 3: simulate competition
        simulateCompResponse =
                given()
                        .port(port)
                        .param("times", "1")
                        .when()
                        .post("/%s/simulate".formatted(compId))
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //// check simulated match
        jsonResponse = new JSONObject(simulateCompResponse.asString());
        assertEquals(2, jsonResponse.getJSONArray("rounds").getJSONObject(0).getInt("round"));
        assertEquals(1, jsonResponse.getJSONArray("rounds").getJSONObject(0).getJSONArray("matches").length());

        JSONObject fm2Json = (JSONObject) jsonResponse.getJSONArray("rounds").getJSONObject(0).getJSONArray("matches").get(0);
        assertEquals(compId, fm2Json.getLong("competitionId"));
        assertNotSame(fm2Json.get("matchResult").toString(), FootballMatch.MatchResult.UNFINISHED.toString());

        // STEP 4: attempt to simulate competition (can no longer be simulated)
        given()
                .port(port)
                .when()
                .post("/%s/simulate".formatted(compId))
                .then()
                .statusCode(400);

        // Step 5: retrieve matches for competition
        RestAssured.basePath = "";
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/match/%s".formatted(fm1Json.getInt("id")))
                .then()
                .statusCode(200);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/match/%s".formatted(fm2Json.getInt("id")))
                .then()
                .statusCode(200);

        // STEP 5: delete competition (and all of its matches automatically)
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .delete("/competition/%s".formatted(compId))
                .then()
                .statusCode(200);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/competition/%s".formatted(compId))
                .then()
                .statusCode(400);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/match/%s".formatted(fm1Json.getInt("id")))
                .then()
                .statusCode(400);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/match/%s".formatted(fm2Json.getInt("id")))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should create clubs and then attempt creating competitions with them")
    void shouldCreateClubsAndCompetition() {
        // Arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(clubRepository.saveAll(
                List.of(
                        FootballClub.builder().name("Club 1").country(CountryCode.GB).victoryChance(0.5).build(),
                        FootballClub.builder().name("Club 2").country(CountryCode.GB).victoryChance(0.5).build(),
                        FootballClub.builder().name("Club 3").country(CountryCode.ES).victoryChance(0.5).build(),
                        FootballClub.builder().name("Club 4").country(CountryCode.ES).victoryChance(0.5).build(),
                        FootballClub.builder().name("Club 5").country(CountryCode.ES).victoryChance(0.5).build(),
                        FootballClub.builder().name("Club 6").country(CountryCode.ES).victoryChance(0.5).build()
                )));

        // STEP 1 - not enough clubs to fill for given country - should fail
        var compRequest = new CreateCompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2024-01-01",
                "2024-10-01",
                null,
                4, new SimulationValues(0));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(compRequest)
                .when()
                .post("")
                .then()
                .statusCode(400);

        // STEP 2 - not enough clubs to fill even with IDs - should fail
        compRequest = new CreateCompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2024-01-01",
                "2024-10-01",
                ids.subList(0, 2),
                4, new SimulationValues(0));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(compRequest)
                .when()
                .post("")
                .then()
                .statusCode(400);

        // STEP 3 - enough clubs to fill but can't mix countries - should fail
        compRequest = new CreateCompetitionRequestDTO("Competition",
                CountryCode.ES,
                "2024-01-01",
                "2024-10-01",
                ids.subList(0, 2),
                4, new SimulationValues(0));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(compRequest)
                .when()
                .post("")
                .then()
                .statusCode(400);

        compRequest = new CreateCompetitionRequestDTO("Competition",
                CountryCode.GB,
                "2024-01-01",
                "2024-10-01",
                ids.subList(3, 6),
                null, new SimulationValues(0));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(compRequest)
                .when()
                .post("")
                .then()
                .statusCode(400);

        // STEP 4 - enough clubs - should succeed
        compRequest = new CreateCompetitionRequestDTO("Competition",
                CountryCode.ES,
                "2024-01-01",
                "2024-10-01",
                ids.subList(3, 4),
                4, new SimulationValues(0));

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(compRequest)
                .when()
                .post("")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("lastSimulatedRound", equalTo(0))
                .body("name", equalTo(compRequest.name()))
                .body("startDate", equalTo(compRequest.startDate()))
                .body("endDate", equalTo(compRequest.endDate()))
                .body("country", equalTo(compRequest.country().toString()))
                .body("simulationValues.variance", equalTo(0.0F))
                .body("simulationValues.scoreDifferenceDrawTrigger", equalTo(0.0F))
                .body("simulationValues.drawTriggerChance", equalTo(0.0F));
    }
}
