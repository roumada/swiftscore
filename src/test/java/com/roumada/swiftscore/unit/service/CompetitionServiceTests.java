package com.roumada.swiftscore.unit.service;


import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;

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
        when(fcdl.findAllById(ids)).thenReturn(List.of(fc1, fc2));
        var dto = new CompetitionRequestDTO("",
                CountryCode.GB,
                "2025-01-01",
                "2025-12-30",
                ids,
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
    @DisplayName("Generate competition - invalid IDs - should return error message")
    void generateCompetition_invalidIds_shouldGenerateError() {
        // arrange
        var ids = List.of(1L, 2L);
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.2f).build();
        fc1.setId(1L);
        when(fcdl.findAllById(ids)).thenReturn(List.of(fc1));
        var dto = new CompetitionRequestDTO("",
                CountryCode.GB,
                "2025-01-01",
                "2025-12-30",
                ids,
                new SimulationValues(0));

        // act
        var optionalComp = service.generateAndSave(dto);

        // assert
        assertTrue(optionalComp.isLeft());
        assertEquals(String.class, optionalComp.getLeft().getClass());
    }

    @Test
    @DisplayName("Simulate competition - for a competition with four clubs - " +
            "matches should be properly resolved")
    void generateStandings_forFullySimulatedCompetition_shouldReturnResolved() {
        // arrange
        when(sequenceService.getNextValue()).thenAnswer((Answer<Long>) invocationOnMock -> increment());
        var ids = List.of(0L, 1L, 2L, 3L);
        when(fcdl.findAllById(ids)).thenReturn(FootballClubTestUtils.getFourFootballClubs(true));
        var comp = service.generateAndSave(new CompetitionRequestDTO("",
                CountryCode.GB,
                "2025-01-01",
                "2025-12-30",
                ids,
                new SimulationValues(0))).get();

        for (int i = 0; i < comp.getParticipants().size() * 2 - 2; i++) {
            // act
            var eitherSimulatedRound = service.simulateRound(comp);

            // assert
            assert (eitherSimulatedRound).isRight();

            var simulatedRound = eitherSimulatedRound.get();
            assertNotNull(simulatedRound);
            assertEquals(i + 1, simulatedRound.getRound());

            for (int a = 0; a < comp.getParticipants().size() / 2; a++) {
                assertNotEquals(FootballMatch.MatchResult.UNFINISHED, simulatedRound.getMatches().get(a).getMatchResult());
            }
        }
    }

    private Long increment() {
        id++;
        return id;
    }
}
