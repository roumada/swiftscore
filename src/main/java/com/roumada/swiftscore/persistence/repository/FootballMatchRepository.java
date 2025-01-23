package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.FootballMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FootballMatchRepository extends MongoRepository<FootballMatch, Long> {


    @Query("""
              {'$and': [
              { '$or': [
                  { 'homeSideFootballClub.id': ?0 },
                  { 'awaySideFootballClub.id': ?0 }
                ]},
            ]}
            """)
    Page<FootballMatch> findByFootballClubId(long footballClubId, Pageable pageable);

    @Query("""
            {'$and': [
            { '$or': [
                { 'homeSideFootballClub.id': ?0 },
                { 'awaySideFootballClub.id': ?0 }
              ]},
                { 'matchResult': {$ne: 'UNFINISHED'} }
              ]}
            """)
    Page<FootballMatch> findByFootballClubIdExcludeUnfinished(long footballClubId, Pageable pageable);

    @Query("""
              {'$and': [
              { 'competitionId': ?0 },
              { '$or': [
                  { 'homeSideFootballClub.id': ?1 },
                  { 'awaySideFootballClub.id': ?1 }
                ]},
            ]}
            """)
    Page<FootballMatch> findByCompetitionIdAndFootballClubId(long competitionId, long footballClubId, PageRequest pageRequest);

    @Query("""
              {'$and': [
              { 'competitionId': ?0 },
              { '$or': [
                  { 'homeSideFootballClub.id': ?1 },
                  { 'awaySideFootballClub.id': ?1 }
                ]},
              { 'matchResult': {$ne: 'UNFINISHED'} }
            ]}
            """)
    Page<FootballMatch> findByCompetitionIdAndFootballClubIdExcludeUnfinished(long competitionId, long footballClubId, PageRequest pageRequest);


    void deleteByCompetitionId(long id);
}
