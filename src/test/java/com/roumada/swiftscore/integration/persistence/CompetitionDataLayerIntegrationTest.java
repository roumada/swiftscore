package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionDataLayerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private CompetitionRepository repository;
    @Autowired
    private CompetitionRoundRepository compRoundRepository;

    @Test
    @DisplayName("Should save a competition to the database")
    void shouldSaveCompetition() {
        var comp = new Competition(Collections.emptyList(), Collections.emptyList());

        var saved = repository.save(comp);
        var retrieved = repository.findById(saved.getId());

        assertThat(retrieved).isPresent();

        comp = retrieved.get();
        assertEquals(10000, comp.getId());
        assertTrue(comp.getRounds().isEmpty());
        assertTrue(comp.getParticipants().isEmpty());
    }

    @Test
    @DisplayName("Should save a competition with competition rounds to the database")
    void shouldSaveWithCompRounds() {
        var compRound1 = CompetitionRound.builder().round(1).matches(Collections.emptyList()).build();
        var compRound2 = CompetitionRound.builder().round(2).matches(Collections.emptyList()).build();
        var comp = new Competition(Collections.emptyList(), List.of(compRound1, compRound2));

        compRoundRepository.save(compRound1);
        compRoundRepository.save(compRound2);
        var saved = repository.save(comp);
        var retrieved = repository.findById(saved.getId());

        assertThat(retrieved).isPresent();

        comp = retrieved.get();
        assertEquals(2, comp.getRounds().size());
    }
}
