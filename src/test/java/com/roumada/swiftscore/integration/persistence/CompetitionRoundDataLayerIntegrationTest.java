package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionRoundDataLayerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitionRoundRepository repository;

    @Test
    @DisplayName("Should save a competition round to the database")
    void shoudSaveToDatabase() {
        var compRound = CompetitionRound.builder().round(1).matches(Collections.emptyList()).build();

        var saved = repository.save(compRound);
        var optionalCompRound = repository.findById(saved.getId());

        assertThat(optionalCompRound).isPresent();

        var retrieved = optionalCompRound.get();
        assertThat(retrieved.getId()).isEqualTo(compRound.getId());
    }
}
