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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var competition = CompetitionTestUtils.getForPersistedClubs(clubs);
        competition.setRounds(Collections.emptyList());

        // act
        competition = competitionDataLayer.save(competition);

        // assert
        assertTrue(competitionRepo.findById(competition.getId()).isPresent());
    }

    @Test
    @DisplayName("Find a competition by ID - should find")
    void findACompetitionById_shouldFind() {
        // arrange
        var clubs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = CompetitionTestUtils.getForPersistedClubs(clubs);
        competition.setRounds(Collections.emptyList());
        var savedId = competitionRepo.save(competition).getId();

        // act
        var findResult = competitionDataLayer.findCompetitionById(savedId);

        // assert
        assertTrue(findResult.isPresent());
        var found = findResult.get();
        assertEquals(savedId, found.getId());
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
        assertEquals(2, comps.size());
        assertEquals(ids, comps.stream().map(Competition::getId).toList());
    }

    @Test
    @DisplayName("Delete competition - should delete")
    void deleteCompetition_shouldDelete(){
        // arrange
        var clubs = clubRepo.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = CompetitionTestUtils.getForPersistedClubs(clubs);
        competition.setRounds(Collections.emptyList());
        competition = competitionRepo.save(competition);
        // pre-assert
        assertTrue(competitionRepo.findById(competition.getId()).isPresent());

        // act
        competitionDataLayer.delete(competition.getId());

        // assert
        assertTrue(competitionRepo.findById(competition.getId()).isEmpty());
    }

    @Test
    @DisplayName("Search for competition - by name - should find")
    void searchCompetition_name_shouldFind(){
        // arrange
        loadCompetitionWithFcs();

        // act
        var name = "azure";
        var comps = competitionDataLayer.findByName(name, Pageable.ofSize(20)).getContent();

        // assert
        assertEquals(3, comps.size());
        assertEquals(3, comps.stream().filter(x -> x.getName().toLowerCase().contains(name)).toList().size());
    }

    @Test
    @DisplayName("Search for competition - by country code - should find")
    void searchCompetition_season_shouldFind(){
        // arrange
        loadCompetitionWithFcs();

        // act
        var season = "2025";
        var comps = competitionDataLayer.findBySeason(season, Pageable.ofSize(20)).getContent();

        // assert
        assertEquals(3, comps.size());
        assertEquals(3, comps.stream().filter(x -> x.getSeason().equals(season)).toList().size());
    }

    @Test
    @DisplayName("Search for competition - by season - should find")
    void searchCompetition_countryCode_shouldFind(){
        // arrange
        loadCompetitionWithFcs();

        // act
        var cc = CountryCode.ES;
        var comps = competitionDataLayer.findByCountry(cc, Pageable.ofSize(20)).getContent();

        // assert
        assertEquals(2, comps.size());
        assertEquals(2, comps.stream().filter(x -> x.getCountry() == cc).toList().size());
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
        loadCompetitionWithFcs();
        CountryCode cc = StringUtils.isEmpty(country) ? null : CountryCode.valueOf(country);
        SearchCompetitionCriteriaDTO criteria = new SearchCompetitionCriteriaDTO(name, cc, season);
        Pageable pageable = Pageable.ofSize(10);

        // act
        List<Competition> found = competitionDataLayer.searchWithMultipleCriteria(criteria, pageable).toList();

        // assert
        assertEquals(expected, found.size());
        for(Competition c : found){
            if(StringUtils.isNotEmpty(name)){
                assertTrue(c.getName().toLowerCase().contains(name));
            }
            if(StringUtils.isNotEmpty(country)){
                assertEquals(cc, c.getCountry());
            }
            if(StringUtils.isNotEmpty(season)){
                assertEquals(season, c.getSeason());
            }
        }
    }
}
