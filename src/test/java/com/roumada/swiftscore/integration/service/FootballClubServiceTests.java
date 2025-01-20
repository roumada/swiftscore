package com.roumada.swiftscore.integration.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.service.FootballClubService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FootballClubServiceTests extends AbstractBaseIntegrationTest {

    @Autowired
    FootballClubService service;

    @Autowired
    FootballClubRepository footballClubRepository;

    @Test
    @DisplayName("Find by ID - valid ID - should return")
    void findById_validID_shouldReturn() {
        // arrange
        var id = footballClubRepository.save(FootballClubTestUtils.getClub()).getId();

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
        var ids = footballClubRepository
                .saveAll(FootballClubTestUtils.getFourFootballClubs())
                .stream().map(FootballClub::getId).toList();

        // act
        List<FootballClub> clubs = service.findAll();

        // assert
        assertEquals(4, clubs.size());
        assertEquals(ids, clubs.stream().map(FootballClub::getId).toList());
    }

    @Test
    @DisplayName("Save football club - should return")
    void saveFC_shouldReturn() {
        // act
        var fc = service.save(
                new FootballClubDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // assert
        assertNotNull(fc);
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update name - should return")
    void updateFC_updateName_shouldReturn() {
        // arrange
        var fc = service.save(
                new FootballClubDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // act
        var updateResult = service.update(fc.getId(),
                new FootballClubDTO("FC2", null, null, 0.0));

        // assert
        assertTrue(updateResult.isRight());
        fc = updateResult.get();
        assertEquals("FC2", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update country - should return")
    void updateFC_updateCountry_shouldReturn() {
        // arrange
        var fc = service.save(
                new FootballClubDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // act
        var updateResult = service.update(fc.getId(),
                new FootballClubDTO(null, CountryCode.PL, null, 0.0));

        // assert
        assertTrue(updateResult.isRight());
        fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.PL, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update stadium name - should return")
    void updateFC_updateStadiumName_shouldReturn() {
        // arrange
        var fc = service.save(
                new FootballClubDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // act
        var updateResult = service.update(fc.getId(),
                new FootballClubDTO(null, null, "FC Park", 0.0));

        // assert
        assertTrue(updateResult.isRight());
        fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Park", fc.getStadiumName());
        assertEquals(0.5, fc.getVictoryChance());
    }

    @Test
    @DisplayName("Update football club - update victory chance - should return")
    void updateFC_updateVictoryChance_shouldReturn() {
        // arrange
        var fc = service.save(
                new FootballClubDTO("FC", CountryCode.GB, "FC Stadium", 0.5)
        );

        // act
        var updateResult = service.update(fc.getId(),
                new FootballClubDTO(null, null, null, 0.2));

        // assert
        assertTrue(updateResult.isRight());
        fc = updateResult.get();
        assertEquals("FC", fc.getName());
        assertEquals(CountryCode.GB, fc.getCountry());
        assertEquals("FC Stadium", fc.getStadiumName());
        assertEquals(0.2, fc.getVictoryChance());
    }
}
