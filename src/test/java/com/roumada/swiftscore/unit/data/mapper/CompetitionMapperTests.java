package com.roumada.swiftscore.unit.data.mapper;

import com.roumada.swiftscore.data.mapper.CompetitionMapper;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionMapperTests {
    private final CompetitionMapper mapper = CompetitionMapper.INSTANCE;

    @Test
    @DisplayName("Should convert from request to object")
    void shouldConvertFromRequestToObject() {
        // arrange
        var request = new CompetitionRequestDTO(List.of(1L, 2L), 0.0f);

        // act
        var object = mapper.competitionRequestDTOToCompetition(request);

        // assert
        assertEquals(0.0f, object.getVariance());
    }

    @Test
    @DisplayName("Should convert from object to response")
    void shouldConvertFromObjectToResponse() {
        // arrange
        var object = new Competition(List.of(
                FootballClub.builder().name("Norf FC").id(1L).build(),
                FootballClub.builder().name("Souf FC").id(2L).build()
        ), List.of(
                CompetitionRound.builder().id(3L).round(1).build(),
                CompetitionRound.builder().id(4L).round(2).build()
        ), 0.0f);

        // act
        var response = mapper.competitionToCompetitionResponseDTO(object);

        // assert
        assertEquals(List.of(1L, 2L), response.participantIds());
        assertEquals(List.of(3L, 4L), response.roundIds());
    }


}
