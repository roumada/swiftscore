package com.roumada.swiftscore.unit.service;


import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionParametersDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import static com.neovisionaries.i18n.CountryCode.ES;
import static com.neovisionaries.i18n.CountryCode.GB;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTests {

    @Mock
    private CompetitionDataLayer cdl;
    @Mock
    private FootballClubDataLayer fcdl;
    @Mock
    private FootballMatchDataLayer fmdl;
    @Mock
    private CompetitionRoundDataLayer crdl;
    @Mock
    private PrimarySequenceService sequenceService;
    @InjectMocks
    private CompetitionService service;

    private long id = 0;

    @Test
    @DisplayName("Generate competition - valid IDs - should generate")
    void generateCompetition_valid_shouldGenerate() {
        // arrange
        when(sequenceService.getNextValue()).thenAnswer((Answer<Long>) invocationOnMock -> increment());
        var ids = List.of(1L, 2L);
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.2f).build();
        fc1.setId(1L);
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fc2.setId(2L);
        when(fcdl.findAllByIdAndCountry(ids, GB)).thenReturn(List.of(fc1, fc2));
        var dto = new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(0, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isRight());
        var comp = optionalComp.get();
        Long compId = comp.getId();
        assertNotNull(comp.getId());
        assertEquals(List.of(fc1, fc2), comp.getParticipants());
        for (CompetitionRound cr : comp.getRounds()) {
            assertNotNull(cr.getId());
            assertEquals(cr.getCompetitionId(), compId);
            for (FootballMatch fm : cr.getMatches()) {
                assertNotNull(fm.getCompetitionId());
                assertEquals(fm.getCompetitionId(), compId);
                assertNotNull(fm.getCompetitionRoundId());
                assertEquals(fm.getCompetitionRoundId(), cr.getId());
            }
        }
    }

    @Test
    @DisplayName("Generate competition - valid IDs and greater participants parameter - should generate")
    void generateCompetition_validIdsAndGreaterFillToParticipantsParameter_shouldGenerate() {
        // arrange
        when(sequenceService.getNextValue()).thenAnswer((Answer<Long>) invocationOnMock -> increment());
        var ids = List.of(0L, 1L);
        var clubs = FootballClubTestUtils.getFourFootballClubs(true);
        when(fcdl.findAllByIdAndCountry(ids, GB)).thenReturn(FootballClubTestUtils.getFourFootballClubs(true).subList(0, 2));
        when(fcdl.findByIdNotInAndCountryIn(ids, GB, 2)).thenReturn(FootballClubTestUtils.getFourFootballClubs(true).subList(2, 4));
        var dto = new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(4, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isRight());
        var comp = optionalComp.get();
        Long compId = comp.getId();
        assertNotNull(comp.getId());
        assertEquals(clubs, comp.getParticipants());
        for (CompetitionRound cr : comp.getRounds()) {
            assertNotNull(cr.getId());
            assertEquals(cr.getCompetitionId(), compId);
            for (FootballMatch fm : cr.getMatches()) {
                assertNotNull(fm.getCompetitionId());
                assertEquals(fm.getCompetitionId(), compId);
                assertNotNull(fm.getCompetitionRoundId());
                assertEquals(fm.getCompetitionRoundId(), cr.getId());
            }
        }
    }

    @Test
    @DisplayName("Generate competition - uneven ID amount - should return error message")
    void generateCompetition_unevenIdAmount_shouldGenerateError() {
        // arrange
        var ids = List.of(1L, 2L, 3L);
        var dto = new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(0, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.", optionalComp.getLeft());
    }

    @Test
    @DisplayName("Generate competition - uneven participants value greater than Ids - should return error message")
    void generateCompetition_unevenGreaterFillToParticipantsAmount_shouldGenerateError() {
        // arrange
        var ids = List.of(1L, 2L, 3L, 4L);
        var dto = new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(7, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Failed to generate competition - the amount of clubs participating must be even.", optionalComp.getLeft());
    }

    @Test
    @DisplayName("Generate competition - invalid IDs - should return error message")
    void generateCompetition_invalidIds_shouldGenerateError() {
        // arrange
        var ids = List.of(1L, 2L);
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.2f).build();
        fc1.setId(1L);
        when(fcdl.findAllByIdAndCountry(ids, GB)).thenReturn(List.of(fc1));
        var dto = new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(0, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Couldn't retrieve all clubs for given IDs and country.", optionalComp.getLeft());
    }

    @Test
    @DisplayName("Generate competition - competition and league country mismatch - should return error message")
    void generateCompetition_invalidCountries_shouldGenerateError() {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(FootballClubTestUtils.getTwoFootballClubs());
        when(fcdl.findAllByIdAndCountry(ids, ES)).thenReturn(Collections.emptyList());
        var dto = new CreateCompetitionRequestDTO("",
                ES,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(0, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Couldn't retrieve all clubs for given IDs and country.", optionalComp.getLeft());
    }

    @Test
    @DisplayName("Generate competition - not enough clubs to fill with fc IDs - should return error message")
    void generateCompetition_notEnoughClubsWithFCIds_shouldGenerateError() {
        // arrange
        var ids = FootballClubTestUtils.getIdsOfSavedClubs(FootballClubTestUtils.getTwoFootballClubs());
        when(fcdl.findAllByIdAndCountry(ids, ES)).thenReturn(FootballClubTestUtils.getTwoFootballClubs());
        when(fcdl.findByIdNotInAndCountryIn(ids, ES, 2)).thenReturn(Collections.emptyList());
        var dto = new CreateCompetitionRequestDTO("",
                ES,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(4, ids, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Couldn't find enough clubs from given country to fill in the league.", optionalComp.getLeft());
    }

    @Test
    @DisplayName("Generate competition - not enough clubs to fill without fc IDs - should return error message")
    void generateCompetition_notEnoughClubsWithoutFCIds_shouldGenerateError() {
        // arrange
        when(fcdl.findByIdNotInAndCountryIn(Collections.emptyList(), ES, 4)).thenReturn(FootballClubTestUtils.getTwoFootballClubs());
        var dto = new CreateCompetitionRequestDTO("",
                ES,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(4, null, 0),
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals("Couldn't find enough clubs from given country to fill in the league.", optionalComp.getLeft());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    @DisplayName("Simulate competition - for a competition with four clubs - " +
            "matches should be properly resolved")
    void generateStandings_forFullySimulatedCompetition_shouldReturnResolved(int times) {
        // arrange
        when(sequenceService.getNextValue()).thenAnswer((Answer<Long>) invocationOnMock -> increment());
        var ids = List.of(0L, 1L, 2L, 3L);
        when(fcdl.findAllByIdAndCountry(ids, GB)).thenReturn(FootballClubTestUtils.getFourFootballClubs(true));
        var comp = service.generateAndSave(new CreateCompetitionRequestDTO("",
                GB,
                "2025-01-01",
                "2025-12-30",
                new CompetitionParametersDTO(0, ids, 0),
                new SimulationValues(0))).get();

        for (int i = 0; i < comp.getParticipants().size() * 2 - 2; i += times) {
            // act
            var eitherSimulatedRounds = service.simulate(comp, times);

            // assert
            assert (eitherSimulatedRounds).isRight();

            var simulatedRounds = eitherSimulatedRounds.get();
            assertNotNull(simulatedRounds);
            assertEquals(times, simulatedRounds.size());
            for (int a = 0; a < times; a++) {
                assertEquals(i + a + 1, simulatedRounds.get(a).getRound());
                for (int b = 0; b < comp.getParticipants().size() / 2; b++) {
                    assertNotEquals(FootballMatch.MatchResult.UNFINISHED, simulatedRounds.get(a).getMatches().get(b).getMatchResult());
                }
            }
        }
    }

    private Long increment() {
        id++;
        return id;
    }
}
