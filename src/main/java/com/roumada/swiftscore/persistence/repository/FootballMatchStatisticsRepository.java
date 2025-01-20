package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FootballMatchStatisticsRepository extends MongoRepository<FootballMatchStatistics, Long> {

    @Query("""
              {'$and': [
              { 'footballClubId': ?0 },
              { 'result': {$ne: 'UNFINISHED'} }
            ]}
            """)
    Page<FootballMatchStatistics> findByFootballClubIdExcludeUnfinished(Long footballClubId, Pageable pageable);

    Page<FootballMatchStatistics> findByFootballClubId(Long footballClubId, Pageable pageable);

    @Query("""
              {'$and': [
              { 'competitionId': ?0 },
              { 'footballClubId': ?1 },
              { 'result': {$ne: 'UNFINISHED'} }
            ]}
            """)
    Page<FootballMatchStatistics>
    findByFootballClubIdAndCompetitionIdExcludeUnfinished(Long competitionId, Long footballClubId, Pageable pageable);

    Page<FootballMatchStatistics>
    findByFootballClubIdAndCompetitionId(Long competitionId, Long footballClubId, Pageable pageable);

    void deleteByCompetitionId(long id);
}
