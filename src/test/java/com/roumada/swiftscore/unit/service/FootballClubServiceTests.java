package com.roumada.swiftscore.unit.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.FootballClubRequestDTO;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.service.FootballClubService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FootballClubServiceTests extends AbstractBaseIntegrationTest {

    @Mock
    FootballClubRepository footballClubRepository;
    @InjectMocks
    FootballClubService service;

    @Test
    @DisplayName("Find by ID - valid ID - should return")
    void findById_validID_shouldReturn() {
        // arrange
        var id = 1L;
        when(footballClubRepository.findById(id)).thenReturn(Optional.of(FootballClubTestUtils.getClub(true)));

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
        when(footballClubRepository.findAll()).thenReturn(FootballClubTestUtils.getFourFootballClubs(true));

        // act
        List<FootballClub> clubs = service.findAll();

        // assert
        assertEquals(4, clubs.size());
        assertEquals(ids, clubs.stream().map(FootballClub::getId).toList());
    }

    @Test
    @DisplayName("Find all by IDs - should return")
    void findAllByIds_shouldReturn() {
        // arrange
        var ids = FootballClubTestUtils.getFourFootballClubs(true).stream().map(FootballClub::getId).toList();
        when(footballClubRepository.findAllById(ids)).thenReturn(FootballClubTestUtils.getFourFootballClubs(true));

        // act
        List<FootballClub> clubs = service.findAllByIds(ids);

        // assert
        assertEquals(4, clubs.size());
        assertEquals(ids, clubs.stream().map(FootballClub::getId).toList());
    }

    @Test
    @DisplayName("Save football club - should return")
    void saveFC_shouldReturn() {
        // arrange
        var fcSaved = FootballClub.builder().name("FC").country(CountryCode.GB).stadiumName("FC Stadium").victoryChance(0.5).build();
        when(footballClubRepository.save(fcSaved)).thenReturn(fcSaved);

        // act
        var fc = service.save(
                new FootballClubRequestDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // assert
        assertNotNull(fc);
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
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
        when(footballClubRepository.findById(id)).thenReturn(Optional.of(fcSaved));
        when(footballClubRepository.save(fcUpdated)).thenReturn(fcUpdated);


        // act
        var updateResult = service.update(id,
                new FootballClubRequestDTO("FC2", null, null, 0.0));

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
        when(footballClubRepository.findById(id)).thenReturn(Optional.of(fcSaved));
        when(footballClubRepository.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new FootballClubRequestDTO(null, CountryCode.PL, null, 0.0));

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
        when(footballClubRepository.findById(id)).thenReturn(Optional.of(fcSaved));
        when(footballClubRepository.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new FootballClubRequestDTO(null, null, "FC Park", 0.0));

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
        when(footballClubRepository.findById(id)).thenReturn(Optional.of(fcSaved));
        when(footballClubRepository.save(fcUpdated)).thenReturn(fcUpdated);

        // act
        var updateResult = service.update(id,
                new FootballClubRequestDTO(null, null, null, 0.2));

        // assert
        assertTrue(updateResult.isRight());
        var fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.2, fc.getVictoryChance());
    }
}
