package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FootballMatchRepository extends MongoRepository<FootballMatch, Long> {
    List<FootballMatch> getForCompetitionAndRound(Competition competition, int round);
}
