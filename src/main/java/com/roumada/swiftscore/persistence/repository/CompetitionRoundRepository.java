package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.organization.CompetitionRound;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetitionRoundRepository extends MongoRepository<CompetitionRound, Long> {
    void deleteByCompetitionId(long id);
}
