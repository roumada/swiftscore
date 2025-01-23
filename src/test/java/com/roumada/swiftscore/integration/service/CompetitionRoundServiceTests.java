package com.roumada.swiftscore.integration.service;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.service.CompetitionRoundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionRoundServiceTests extends AbstractBaseIntegrationTest {

    @Autowired
    CompetitionRoundService service;
    @Autowired
    CompetitionRoundRepository repository;

    @Test
    @DisplayName("Get round - valid ID - should return")
    void getRound_validId_shouldReturn(){
        // arrange
        var id = repository.save(new CompetitionRound()).getId();

        // act
        var findResult = service.findById(id);

        // assert
        assertTrue(findResult.isRight());
        assertEquals(id, findResult.get().getId());
    }

    @Test
    @DisplayName("Get round - invalid ID - should return error message")
    void getRound_invalidId_shouldReturnErrorMsg(){
        // act
        var findResult = service.findById(0);

        // assert
        assertTrue(findResult.isLeft());
    }
}
