package com.roumada.swiftscore.unit.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.model.organization.league.LeagueSeason;
import com.roumada.swiftscore.persistence.datalayer.LeagueDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.service.LeagueSeasonService;
import com.roumada.swiftscore.service.LeagueService;
import io.vavr.control.Either;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.roumada.swiftscore.util.LeagueTestUtils.getCreateLeagueCompetitionRequests;
import static com.roumada.swiftscore.util.LeagueTestUtils.getEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTests {

    @Mock
    LeagueDataLayer dataLayer;
    @Mock
    LeagueSeasonService leagueSeasonService;
    @Mock
    CompetitionService competitionService;
    @InjectMocks
    LeagueService service;

    @Test
    @DisplayName("Create league from request - should create")
    void createLeagueFromRequest_shouldCreate() {
        // arrange
        var c1id = 100L;
        var c2id = 200L;
        var leagueId = 10L;
        Competition c1 = Competition.builder().build();
        c1.setId(c1id);
        c1.setParticipants(Collections.emptyList());
        Competition c2 = Competition.builder().build();
        c2.setId(c2id);
        c2.setParticipants(Collections.emptyList());
        var leagueRequest = new CreateLeagueRequest(
                "Name",
                CountryCode.GB,
                "2020-01-01",
                "2020-06-01",
                getCreateLeagueCompetitionRequests(4, 6)
        );

        when(leagueSeasonService.generateNew(any())).thenReturn(Either.right(new LeagueSeason("2020", List.of(c1id, c2id))));
        when(dataLayer.save(any())).thenAnswer(invocation -> {
            League league = invocation.getArgument(0);
            league.setId(leagueId);
            return league;
        });

        // act
        var result = service.createFromRequest(leagueRequest);

        // assert
        assertThat(result.isRight()).isTrue();
        var league = result.get();
        assertThat(league.getId()).isEqualTo(leagueId);
        assertThat(league.getSeasons().get(0).competitionIds()).contains(c1id, c2id);
    }


    @Test
    @DisplayName("Create league from request - errors during competition creation - should return error message")
    void createLeagueFromRequest_errorsDuringCompetitionCreation_shouldReturnErrorMessages() {
        // arrange
        var errorMsg1 = "Error 1";
        var errorMsg2 = "Error 2";
        var leagueRequest = new CreateLeagueRequest(
                "Name",
                CountryCode.GB,
                "2020-01-01",
                "2020-06-01",
                getCreateLeagueCompetitionRequests(4, 6)
        );
        when(leagueSeasonService.generateNew(any())).thenReturn(Either.left(new ErrorResponse(List.of(errorMsg1, errorMsg2))));

        // act
        var result = service.createFromRequest(leagueRequest);

        // assert
        assertThat(result.isLeft()).isTrue();
        var errorMsgs = result.getLeft();
        assertThat(errorMsgs.requestErrors()).contains(errorMsg1, errorMsg2);
    }

    @Test
    @DisplayName("Find by ID - should find")
    void findById_shouldFind() {
        // arrange
        var id = 10000L;
        var league = getEmpty();
        league.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(league));

        // act
        var result = service.findById(id);

        // assert
        assertThat(result.isRight()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Find by ID - invalid ID - should find error message")
    void findById_invalidId_shouldReturn() {
        // arrange
        var id = 0L;
        when(dataLayer.findById(id)).thenReturn(Optional.empty());

        // act
        var result = service.findById(id);

        // assert
        assertThat(result.isLeft()).isTrue();
    }
}
