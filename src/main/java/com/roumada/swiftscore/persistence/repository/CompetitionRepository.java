package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.Competition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetitionRepository extends MongoRepository<Competition, Long> {
}
