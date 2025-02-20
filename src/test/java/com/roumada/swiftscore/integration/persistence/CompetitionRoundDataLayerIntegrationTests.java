package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionRoundDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private CompetitionRoundRepository competitionRoundRepository;

    @Autowired
    private CompetitionRoundDataLayer competitionRoundDataLayer;

    @Test
    @DisplayName("Save round - should save")
    void saveRound_shouldSave() {
        // arrange
        var round = new CompetitionRound(1, Collections.emptyList());

        // act
        var id = competitionRoundDataLayer.save(round).getId();

        // assert
        var result = competitionRoundRepository.findById(id);
        assertThat(result).isPresent();
        var found = result.get();
        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getRound()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find by ID - should find")
    void findById_shouldFind() {
        // arrange
        var competition = loadCompetitionWithFcs();
        var roundId = competition.getRounds().stream().map(CompetitionRound::getId).toList().get(0);

        // act
        var result = competitionRoundDataLayer.findById(roundId);

        // assert
        assertThat(result).isPresent();
        var round = result.get();
        assertThat(round.getId()).isEqualTo(roundId);
    }

    @Test
    @DisplayName("Delete by competition ID = should delete")
    void deleteByCompetitionId_shouldDelete() {
        // arrange
        var competition = loadCompetitionWithFcs();
        var roundIds = competition.getRounds().stream().map(CompetitionRound::getId).toList();
        // pre-assert
        assertThat(competitionRoundRepository.findAllById(roundIds)).hasSameSizeAs(roundIds);

        // act
        competitionRoundDataLayer.deleteByCompetitionId(competition.getId());

        // assert
        assertThat(competitionRoundRepository.findAllById(roundIds)).isEmpty();
    }
}
