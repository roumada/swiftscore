package com.roumada.swiftscore.persistence.repository;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FootballClubRepository extends MongoRepository<FootballClub, Long> {

    List<FootballClub> findByIdNotInAndCountryIn(List<Long> footballClubIds, CountryCode country, PageRequest pageable);

    List<FootballClub> findAllByIdInAndCountryIn(List<Long> ids, CountryCode country);

    Page<FootballClub> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<FootballClub> findByStadiumNameContainingIgnoreCase(String stadiumName, Pageable pageable);

    Page<FootballClub> findByCountry(CountryCode country, Pageable pageable);

    @Query(value = """
        {
            $and: [
                { $or: [ { name: { $exists: false } }, { name: null }, '$options' : 'i'}, { name: /.?0./ } ] },
                { $or: [ { country: { $exists: false } }, { country: null }, '$options' : 'i'}, { country: ?1 } ] },
                { $or: [ { stadiumName: { $exists: false } }, { stadiumName: null }, '$options' : 'i'}, { stadiumName: ?2 } ] }
            ]
        }
    """)
    Page<FootballClub> findByOptionalCriteria(String name, CountryCode country, String stadiumName, Pageable pageable);

}
