package com.roumada.swiftscore.unit.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteria;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;
import com.roumada.swiftscore.persistence.datalayer.FootballClubDataLayer;
import com.roumada.swiftscore.service.FootballClubService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballClubServiceTests {

    @Mock
    FootballClubDataLayer dataLayer;
    @InjectMocks
    FootballClubService service;

    @Test
    @DisplayName("Find by ID - valid ID - should return")
    void findById_validID_shouldReturn() {
        // arrange
        var id = 1L;
        when(dataLayer.findById(id)).thenReturn(Optional.of(FootballClubTestUtils.getClub(true)));

        // act
        var findResult = service.findById(id);

        // assert
        assertTrue(findResult.isRight());
        assertEquals(id, findResult.get().getId());
    }

    @Test
    @DisplayName("Find by ID - invalid ID - should return error message")
    void findById_invalidID_shouldReturnErrorMessage() {
        // act
        var findResult = service.findById(-1);

        // assert
        assertTrue(findResult.isLeft());
    }

    @Test
    @DisplayName("Find all - should return")
    void findAll_shouldReturn() {
        // arrange
        var ids = FootballClubTestUtils.getFourFootballClubs(true).stream().map(FootballClub::getId).toList();
        when(dataLayer.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(FootballClubTestUtils.getFourFootballClubs(true)));

        // act
        var searchResult = service.searchClubs(new SearchFootballClubSearchCriteria(null, null, null), Pageable.ofSize(20));
        var clubs = searchResult.getContent();

        // assert
        assertEquals(4, clubs.size());
        assertEquals(ids, clubs.stream().map(FootballClub::getId).toList());
    }

    @Test
    @DisplayName("Save football club - should return")
    void saveFC_shouldReturn() {
        // arrange
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        when(dataLayer.save(fcSaved)).thenReturn(fcSaved);

        // act
        var fc = service.save(
                new CreateFootballClubRequest("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // assert
        assertNotNull(fc);
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
    }

    @Test
    @DisplayName("Update football club - invalid ID - should return error code")
    void updateFC_invalidID_shouldReturnErrorCode() {
        // arrange
        when(dataLayer.findById(0L)).thenReturn(Optional.empty());

        // act
        var updateResult = service.update(0L,
                new CreateFootballClubRequest("FC2", null, null, 0.0));

        // assert
        assertTrue(updateResult.isLeft());
        var error = updateResult.getLeft();
        assertEquals("Unable to find football club with given id [0]", error);
    }

    @Test
    @DisplayName("Update football club - update name - should return")
    void updateFC_updateName_shouldReturn() {
        // arrange
        var id = 1L;
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        var fcUpdated = FootballClub.builder().name("FC2").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        fcSaved.setId(id);
        fcUpdated.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(fcSaved));
        when(dataLayer.save(fcUpdated)).thenReturn(fcUpdated);


        // act
        var updateResult = service.update(id,
                new CreateFootballClubRequest("FC2", null, null, 0.0));

        // assert
        assertTrue(updateResult.isRight());
        var fc = updateResult.get();
        assertEquals("FC2", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update country - should return")
    void updateFC_updateCountry_shouldReturn() {
        // arrange
        var id = 1L;
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        var fcUpdated = FootballClub.builder().name("FC").country(CountryCode.PL).stadiumName("FC Stadium").victoryChance(0.5).build();
        fcSaved.setId(id);
        fcUpdated.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(fcSaved));
        when(dataLayer.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new CreateFootballClubRequest(null, CountryCode.PL, null, 0.0));

        // assert
        assertTrue(updateResult.isRight());
        var fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.PL, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update stadium name - should return")
    void updateFC_updateStadiumName_shouldReturn() {
        // arrange
        var id = 1L;
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        var fcUpdated = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Park").victoryChance(0.5).build();
        fcSaved.setId(id);
        fcUpdated.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(fcSaved));
        when(dataLayer.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new CreateFootballClubRequest(null, null, "FC Park", 0.0));

        // assert
        assertTrue(updateResult.isRight());
        var fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Park", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update victory chance - should return")
    void updateFC_updateVictoryChance_shouldReturn() {
        // arrange
        var id = 1L;
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        var fcUpdated = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.2).build();
        fcSaved.setId(id);
        fcUpdated.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(fcSaved));
        when(dataLayer.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new CreateFootballClubRequest(null, null, null, 0.2));

        // assert
        assertTrue(updateResult.isRight());
        var fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.2, fc.getVictoryChance());
    }
}
