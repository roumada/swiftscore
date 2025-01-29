package com.roumada.swiftscore.unit.service;

import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.service.CompetitionRoundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetitionRoundServiceTests {

    @Mock
    CompetitionRoundDataLayer dataLayer;
    @InjectMocks
    CompetitionRoundService service;

    @Test
    @DisplayName("Get round - valid ID - should return")
    void getRound_validId_shouldReturn() {
        // arrange
        long id = 1;
        var round = new CompetitionRound(1, Collections.emptyList());
        round.setId(id);
        when(dataLayer.findById(id)).thenReturn(Optional.of(round));

        // act
        var findResult = service.findById(id);

        // assert
        assertTrue(findResult.isRight());
        assertEquals(id, findResult.get().getId());
        assertEquals(1, findResult.get().getRound());
    }

    @Test
    @DisplayName("Get round - invalid ID - should return error message")
    void getRound_invalidId_shouldReturnErrorMsg() {
        // arrange
        long id = 1;
        when(dataLayer.findById(id)).thenReturn(Optional.empty());

        // act
        var findResult = service.findById(id);

        // assert
        assertTrue(findResult.isLeft());
    }
}
