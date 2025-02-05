package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FootballClubDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository repository;

    @Autowired
    private FootballClubDataLayer dataLayer;

    @Test
    @DisplayName("Save a club - should save")
    void saveAClub_shouldSave() {
        // arrange
        var fc = FootballClub.builder().name("FC1").victoryChance(0.33f).build();

        // act
        var saved = dataLayer.save(fc);

        // assert
        var optionalFC = repository.findById(saved.getId());
        assertTrue(optionalFC.isPresent());
        var retrievedFC = optionalFC.get();
        assertEquals(fc.getName(), retrievedFC.getName());
    }

    @Test
    @DisplayName("Save all - should save")
    void saveAll_shouldSave() {
        // act
        var savedIds = repository.saveAll(List.of(
                FootballClub.builder().name("FC1").victoryChance(0.1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.1).build(),
                FootballClub.builder().name("FC3").victoryChance(0.1).build(),
                FootballClub.builder().name("FC4").victoryChance(0.1).build()
        )).stream().map(FootballClub::getId).toList();

        // assert
        assertEquals(4, savedIds.size());
        assertEquals(4, repository.findAllById(savedIds).size());
    }

    @Test
    @DisplayName("Find by ID - should find")
    void findByID_shouldFind() {
        // arrange
        var savedId = repository.save(FootballClub.builder().name("FC1").victoryChance(0.33f).build()).getId();

        // act
        var clubOptional = dataLayer.findById(savedId);

        // assert
        assertTrue(clubOptional.isPresent());
        var retrievedFC = clubOptional.get();
        assertEquals(savedId, retrievedFC.getId());
    }

    @Test
    @DisplayName("Find all by IDs - should find")
    void findAllByIds_shouldSave() {
        // arrange
        var savedIds = repository.saveAll(List.of(
                FootballClub.builder().name("FC1").victoryChance(0.1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.1).build(),
                FootballClub.builder().name("FC3").victoryChance(0.1).build(),
                FootballClub.builder().name("FC4").victoryChance(0.1).build()
        )).stream().map(FootballClub::getId).toList();

        // act
        var foundClubs = dataLayer.findAllById(savedIds);

        // assert
        assertEquals(savedIds.size(), foundClubs.size());
        assertEquals(savedIds, foundClubs.stream().map(FootballClub::getId).toList());
    }

    @Test
    @DisplayName("Find by ID not in X - should find")
    void findByIdNotIn_shouldSave() {
        // arrange
        var savedIds =
                FootballClubTestUtils.getIdsOfSavedClubs(repository.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));
        long excluded = savedIds.get(0);
        savedIds.remove(0);

        // act
        var foundClubs = dataLayer.findByIdNotIn(List.of(excluded), 4);

        // assert
        assertEquals(3, foundClubs.size());
        assertEquals(savedIds, foundClubs.stream().map(FootballClub::getId).toList());
    }

}

