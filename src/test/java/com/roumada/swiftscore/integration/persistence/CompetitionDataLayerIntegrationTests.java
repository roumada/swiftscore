package com.roumada.swiftscore.integration.persistence;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.criteria.SearchCompetitionCriteriaDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.util.CompetitionTestUtils;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository clubRepo;
    @Autowired
    private CompetitionRepository competitionRepo;

    @Autowired
    private CompetitionDataLayer competitionDataLayer;

    @Test
    @DisplayName("Save competition - should save and write ID")
    void saveCompetition_shouldSave() {
        // arrange
        var clubs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = CompetitionTestUtils.get(clubs);
        competition.setRounds(Collections.emptyList());

        // act
        competition = competitionDataLayer.save(competition);

        // assert
        assertThat(competitionRepo.findById(competition.getId())).isPresent();
    }

    @Test
    @DisplayName("Find a competition by ID - should find")
    void findACompetitionById_shouldFind() {
        // arrange
        var clubs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = CompetitionTestUtils.get(clubs);
        competition.setRounds(Collections.emptyList());
        var savedId = competitionRepo.save(competition).getId();

        // act
        var findResult = competitionDataLayer.findCompetitionById(savedId);

        // assert
        assertThat(findResult).isPresent();
        var found = findResult.get();
        assertThat(found.getId()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("Find all competitions - should find")
    void findAllCompetitions_shouldFind() {
        // arrange
        var fcs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var ids = competitionRepo.saveAll(List.of(Competition.builder()
                .name("Competition")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build(), Competition.builder()
                .name("Competition 2")
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build())).stream().map(Competition::getId).toList();

        // act
        var comps = competitionDataLayer.findAllCompetitions(Pageable.ofSize(20)).getContent();

        // assert
        assertThat(comps).hasSize(2);
        assertThat(comps.stream().map(Competition::getId).toList()).isEqualTo(ids);
    }

    @Test
    @DisplayName("Delete competition - should delete")
    void deleteCompetition_shouldDelete(){
        // arrange
        var clubs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = CompetitionTestUtils.get(clubs);
        competition.setRounds(Collections.emptyList());
        competition = competitionRepo.save(competition);
        // pre-assert
        assertTrue(competitionRepo.findById(competition.getId()).isPresent());

        // act
        competitionDataLayer.delete(competition.getId());

        // assert
        assertThat(competitionRepo.findById(competition.getId())).isEmpty();
    }

    @Test
    @DisplayName("Search for competition - by name - should find")
    void searchCompetition_name_shouldFind(){
        // arrange
        loadCompetitionsWithFcs();
        var expected = 3;

        // act
        var name = "azure";
        var comps = competitionDataLayer.findByName(name, Pageable.ofSize(20)).getContent();

        // assert
        assertThat(comps).hasSize(expected);
        assertThat(comps.stream().filter(x -> x.getName().toLowerCase().contains(name)).count()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Search for competition - by country code - should find")
    void searchCompetition_season_shouldFind(){
        // arrange
        loadCompetitionsWithFcs();
        var expected = 3;

        // act
        var season = "2025";
        var comps = competitionDataLayer.findBySeason(season, Pageable.ofSize(20)).getContent();

        // assert
        assertThat(comps).hasSize(expected);
        assertThat(comps.stream().filter(x -> x.getSeason().equals(season)).count()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Search for competition - by season - should find")
    void searchCompetition_countryCode_shouldFind(){
        // arrange
        loadCompetitionsWithFcs();
        var expected = 2;

        // act
        var cc = CountryCode.ES;
        var comps = competitionDataLayer.findByCountry(cc, Pageable.ofSize(20)).getContent();

        // assert
        assertThat(comps).hasSize(expected);
        assertThat(comps.stream().filter(x -> x.getCountry() == cc).count()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "'azure',       '',             '',             3",
            "'',            'GB',           '',             2",
            "'',            '',             '2024/2025',    3",
            "'azure',       'GB',           '',             1",
            "'',            'GB',           '2024/2025',    1",
            "'emerald',     '',             '2025',         1",
            "'ruby',        'ES',           '2024/2025',    1",
    })
    @DisplayName("Find competitions - various criteria - should find")
    void findCompetitions_variousCriteria_shouldFind(String name, String country, String season, int expected){
        // arrange
        loadCompetitionsWithFcs();
        CountryCode cc = StringUtils.isEmpty(country) ? null : CountryCode.valueOf(country);
        SearchCompetitionCriteriaDTO criteria = new SearchCompetitionCriteriaDTO(name, cc, season);
        Pageable pageable = Pageable.ofSize(10);

        // act
        List<Competition> found = competitionDataLayer.findByMultipleCriteria(criteria, pageable).toList();

        // assert
        assertThat(found).hasSize(expected);
        for(Competition c : found){
            if(StringUtils.isNotEmpty(name)){
                assertThat(c.getName().toLowerCase()).contains(name.toLowerCase());
            }
            if(StringUtils.isNotEmpty(country)){
                assertThat(c.getCountry()).isEqualTo(cc);
            }
            if(StringUtils.isNotEmpty(season)){
                assertThat(c.getSeason()).isEqualTo(season);
            }
        }
    }
}
