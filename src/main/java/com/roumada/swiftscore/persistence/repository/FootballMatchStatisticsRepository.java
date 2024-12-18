package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FootballMatchStatisticsRepository extends MongoRepository<FootballMatchStatistics, Long> {
}
