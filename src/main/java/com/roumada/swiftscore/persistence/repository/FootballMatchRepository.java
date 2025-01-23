package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.FootballMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FootballMatchRepository extends MongoRepository<FootballMatch, Long> {
    Page<FootballMatch> findByHomeSideFootballClub_id(long id, Pageable pageable);

    @Query("""
              {'$and': [
              { 'homeSideFootballClub.id': ?0 },
              { 'result': {$ne: 'UNFINISHED'} }
            ]}
            """)
    Page<FootballMatch> findByHomeSideFootballClub_idExcludeUnfinished(long id, Pageable pageable);

    Page<FootballMatch> findByHomeSideFootballClub_idAndCompetitionId(long competitionId, long footballClubId, PageRequest pageRequest);

    @Query("""
              {'$and': [
              { 'competitionId': ?0 },
              { 'homeSideFootballClub.id': ?1 },
              { 'result': {$ne: 'UNFINISHED'} }
            ]}
            """)
    Page<FootballMatch> findByHomeSideFootballClub_idAndCompetitionIdExcludeUnfinished(long competitionId, long footballClubId, PageRequest pageRequest);



    void deleteByCompetitionId(long id);
}
