package com.roumada.swiftscore.repository;

import com.roumada.swiftscore.model.match.Competition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetitionRepository extends MongoRepository<Competition, Long> {
}
