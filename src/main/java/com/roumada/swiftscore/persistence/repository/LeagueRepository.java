package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.organization.league.League;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeagueRepository extends MongoRepository<League, Long> {
}
