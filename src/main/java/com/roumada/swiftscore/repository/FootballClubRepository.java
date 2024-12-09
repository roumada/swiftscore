package com.roumada.swiftscore.repository;

import com.roumada.swiftscore.model.FootballClub;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FootballClubRepository extends MongoRepository<FootballClub, String> {

    Optional<FootballClub> findById(long id);
}
