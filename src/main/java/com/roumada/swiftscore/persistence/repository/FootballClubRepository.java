package com.roumada.swiftscore.persistence.repository;

import com.roumada.swiftscore.data.model.FootballClub;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballClubRepository extends MongoRepository<FootballClub, Long> {
}
