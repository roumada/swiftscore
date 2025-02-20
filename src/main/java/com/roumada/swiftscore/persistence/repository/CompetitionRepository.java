package com.roumada.swiftscore.persistence.repository;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.organization.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetitionRepository extends MongoRepository<Competition, Long> {
    Page<Competition> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Competition> findByCountry(CountryCode country, Pageable pageable);

    Page<Competition> findBySeason(String season, Pageable pageable);
}
