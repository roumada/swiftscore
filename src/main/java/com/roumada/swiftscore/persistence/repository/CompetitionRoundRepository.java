package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.CompetitionRound;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetitionRoundRepository extends MongoRepository<CompetitionRound, Long> {
}
