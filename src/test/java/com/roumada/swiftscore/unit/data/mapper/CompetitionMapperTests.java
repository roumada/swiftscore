package com.roumada.swiftscore.unit.data.mapper;

import com.roumada.swiftscore.model.mapper.CompetitionMapper;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        var object = new Competition(0.0, List.of(fc1, fc2),
                List.of(cr1, cr2));

        // act
        var response = mapper.competitionToCompetitionResponseDTO(object);

        // assert
        assertEquals(List.of(1L, 2L), response.participantIds());
        assertEquals(List.of(3L, 4L), response.roundIds());
    }
}
