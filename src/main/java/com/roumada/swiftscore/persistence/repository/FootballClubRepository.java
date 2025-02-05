package com.roumada.swiftscore.persistence.repository;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FootballClubRepository extends MongoRepository<FootballClub, Long> {

    @Query(value = "{ '_id': { '$nin': ?0 } }")
    List<FootballClub> findByIdNotIn(List<Long> footballClubIds, PageRequest pageable);

    List<FootballClub> findByIdNotInAndCountryIn(List<Long> footballClubIds, CountryCode country, PageRequest pageable);

    List<FootballClub> findAllByIdInAndCountryIn(List<Long> ids, CountryCode country);
}
