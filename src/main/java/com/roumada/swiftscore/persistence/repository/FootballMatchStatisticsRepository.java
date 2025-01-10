package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FootballMatchStatisticsRepository extends MongoRepository<FootballMatchStatistics, Long> {
    List<FootballMatchStatistics> findByFootballClub(FootballClub club);

}
