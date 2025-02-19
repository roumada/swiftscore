package com.roumada.swiftscore.integration.persistence;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteriaDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(optionalFC).isPresent();
        var retrievedFC = optionalFC.get();
        assertThat(retrievedFC.getName()).isEqualTo(fc.getName());
    }

    @Test
    @DisplayName("Save all - should save")
    void saveAll_shouldSave() {
        // act
        var savedIds = dataLayer.saveAll(List.of(
                FootballClub.builder().name("FC1").victoryChance(0.1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.1).build(),
                FootballClub.builder().name("FC3").victoryChance(0.1).build(),
                FootballClub.builder().name("FC4").victoryChance(0.1).build()
        )).stream().map(FootballClub::getId).toList();

        // assert
        assertThat(savedIds).hasSize(4);
        assertThat(repository.findAllById(savedIds)).hasSize(4);
    }

    @Test
    @DisplayName("Find by ID - should find")
    void findByID_shouldFind() {
        // arrange
        var savedId = repository.save(FootballClub.builder().name("FC1").victoryChance(0.33f).build()).getId();

        // act
        var result = dataLayer.findById(savedId);

        // assert
        assertThat(result).isPresent();
        var retrievedFC = result.get();
        assertThat(retrievedFC.getId()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("Find all by IDs and country - should find")
    void findAllByIdsAndCountry_shouldSave() {
        // arrange
        var savedIds = repository.saveAll(List.of(
                FootballClub.builder().name("FC1").country(CountryCode.GB).victoryChance(0.1).build(),
                FootballClub.builder().name("FC2").country(CountryCode.GB).victoryChance(0.1).build(),
                FootballClub.builder().name("FC3").country(CountryCode.GB).victoryChance(0.1).build(),
                FootballClub.builder().name("FC4").country(CountryCode.GB).victoryChance(0.1).build()
        )).stream().map(FootballClub::getId).toList();

        // act
        var foundClubs = dataLayer.findAllByIdAndCountry(savedIds, CountryCode.GB);

        // assert
        assertThat(savedIds).hasSameSizeAs(foundClubs);
        assertThat(foundClubs.stream().map(FootballClub::getId).toList()).isEqualTo(savedIds);
    }

    @Test
    @DisplayName("Find by ID not in X and country in Y - should find")
    void findByIdNotInXAndCountryInY_shouldSave() {
        // arrange
        var savedIds =
                FootballClubTestUtils.getIdsOfSavedClubs(repository.saveAll(FootballClubTestUtils.getFourFootballClubs(false)));
        long excluded = savedIds.get(0);
        savedIds.remove(0);

        // act
        var clubs = dataLayer.findByIdNotInAndCountryIn(List.of(excluded), CountryCode.GB, 4);

        // assert
        assertThat(clubs).hasSize(3);
        assertThat(clubs.stream().map(FootballClub::getId).toList()).isEqualTo(savedIds);
    }

    @Test
    @DisplayName("Find by criteria - name - should find")
    void findByCriteria_name_shouldFind(){
        // arrange
        loadFCs();
        var expected = 2;

        // act
        var criteria = "united";
        var clubs = dataLayer.findByNameContainingIgnoreCase(criteria, Pageable.ofSize(10)).getContent();

        // assert
        assertThat(clubs).hasSize(expected);
        assertThat(clubs.stream().filter(x-> x.getName().toLowerCase().contains(criteria.toLowerCase())).count())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Find by criteria - country - should find")
    void findByCriteria_country_shouldFind(){
        // arrange
        loadFCs();
        var expected = 6;

        // act
        var criteria = CountryCode.GB;
        var clubs = dataLayer.findByCountry(criteria, Pageable.ofSize(10)).getContent();

        // assert
        assertThat(clubs).hasSize(expected);
        assertThat(clubs.stream().filter(x-> x.getCountry() == criteria).count()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Find by criteria - stadium name - should find")
    void findByCriteria_stadium_shouldFind(){
        // arrange
        loadFCs();
        var expected = 2;

        // act
        var criteria = "estadio";
        var clubs = dataLayer.findByStadiumNameContainingIgnoreCase(criteria, Pageable.ofSize(20)).getContent();

        // assert
        assertThat(clubs).hasSize(expected);
        assertThat(clubs.stream().filter(x-> x.getStadiumName().toLowerCase().contains(criteria.toLowerCase())).count())
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "'united',      '',             '',             18,     2",
            "'',            'GB',           '',             18,     6",
            "'',            '',             'A',            10,     10",
            "'',            '',             'A',            18,     15",
            "'wald',        'DE',           '',             18,     2",
            "'',            'GB',           'b',            18,     1",
            "'wald',        '',             'a',            18,     2",
            "'a',           'ES',           'estadio',      18,     2",
    })
    @DisplayName("Find football clubs - various criteria - should find")
    void findFootballClubs_variousCriteria_shouldFind(String name, String country, String stadiumName,
                                                      int pagesize, int expected){
        // arrange
        loadFCs();

        // act
        var cc = StringUtils.isEmpty(country) ? null : CountryCode.valueOf(country);
        var criteria = new SearchFootballClubSearchCriteriaDTO(name, cc, stadiumName);
        var clubs = dataLayer.findByMultipleCriteria(criteria, Pageable.ofSize(pagesize)).getContent();

        // assert
        assertEquals(expected, clubs.size());
        for(FootballClub fc : clubs){
            if(StringUtils.isNotEmpty(fc.getName())){
                assertTrue(fc.getName().toLowerCase().contains(name.toLowerCase()));
            }
            if(StringUtils.isNotEmpty(country)){
                assertEquals(cc, fc.getCountry());
            }
            if(StringUtils.isNotEmpty(fc.getStadiumName())){
                assertTrue(fc.getStadiumName().toLowerCase().contains(stadiumName.toLowerCase()));
            }
        }
    }
}

