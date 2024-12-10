package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FootballMatchStatisticsRepository extends MongoRepository<FootballMatchStatistics, Long> {
    FootballMatchStatistics getForMatchAndFootballClub(FootballMatch match, FootballClub footballClub);
}
