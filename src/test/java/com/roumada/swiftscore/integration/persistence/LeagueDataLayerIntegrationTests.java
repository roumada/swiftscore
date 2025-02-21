package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.persistence.datalayer.LeagueDataLayer;
import com.roumada.swiftscore.persistence.repository.LeagueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.roumada.swiftscore.util.LeagueTestUtils.getEmpty;
import static org.assertj.core.api.Assertions.assertThat;

class LeagueDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private LeagueRepository repository;
    @Autowired
    private LeagueDataLayer dataLayer;

    @Test
    @DisplayName("Save league - should save")
    void saveLeague_shouldSave() {
        // arrange
        var league = getEmpty();

        // act
        var saved = dataLayer.save(league);

        // assert
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Delete league - should delete")
    void deleteLeague_shouldDelete() {
        // arrange
        var leagueId = repository.save(getEmpty()).getId();
        assertThat(repository.findById(leagueId)).isNotEmpty();

        // act
        dataLayer.deleteById(leagueId);

        // assert
        assertThat(repository.findById(leagueId)).isEmpty();
    }

    @Test
    @DisplayName("Find league by ID - should find")
    void findLeagueById_shouldFind() {
        // arrange
        var leagueId = repository.save(getEmpty()).getId();
        assertThat(repository.findById(leagueId)).isNotEmpty();

        // act
        var result = dataLayer.findById(leagueId);

        // assert
        assertThat(result).isNotEmpty();
        var league = result.get();
        assertThat(league.getId()).isEqualTo(leagueId);
    }

    @Test
    @DisplayName("Find league by ID - invalid ID - should return empty")
    void findLeagueById_invalidId_shouldReturnEmpty() {
        // act
        var result = dataLayer.findById(999L);

        // assert
        assertThat(result).isEmpty();
    }
}
