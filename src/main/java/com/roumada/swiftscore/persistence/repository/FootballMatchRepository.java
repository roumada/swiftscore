package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.data.model.match.FootballMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FootballMatchRepository extends MongoRepository<FootballMatch, Long> {
}
