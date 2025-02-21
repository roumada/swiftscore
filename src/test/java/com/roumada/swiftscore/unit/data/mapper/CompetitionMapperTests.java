package com.roumada.swiftscore.unit.data.mapper;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.mapper.CompetitionMapper;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionMapperTests {
    private final CompetitionMapper mapper = CompetitionMapper.INSTANCE;

    @Test
    @DisplayName("Should convert from object to response")
    void shouldConvertFromObjectToResponse() {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").build();
        fc1.setId(1L);
        var fc2 = FootballClub.builder().name("FC2").build();
        fc2.setId(2L);
        var cr1 = CompetitionRound.builder().round(1).build();
        var cr2 = CompetitionRound.builder().round(2).build();
        cr1.setId(3L);
        cr2.setId(4L);
        var object = Competition.builder()
                .name("Competition")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 10, 1))
                .simulationParameters(new SimulationParameters(0))
                .participants(List.of(fc1, fc2))
                .rounds(List.of(cr1, cr2))
                .build();

        // act
        var response = mapper.competitionToCompetitionResponseDTO(object);

        // assert
        assertEquals(List.of(1L, 2L), response.participantIds());
        assertEquals(List.of(3L, 4L), response.roundIds());
    }
}
