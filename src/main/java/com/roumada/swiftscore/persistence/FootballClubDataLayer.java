package com.roumada.swiftscore.persistence;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteria;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballClubDataLayer {

    private final FootballClubRepository repository;
    private final MongoTemplate template;

    public FootballClub save(FootballClub footballClub) {
        var saved = repository.save(footballClub);
        log.debug("Football club with data [{}] saved.", footballClub);
        return saved;
    }

    public List<FootballClub> saveAll(List<FootballClub> fcs) {
        return repository.saveAll(fcs);
    }

    public Optional<FootballClub> findById(long id) {
        return repository.findById(id);
    }

    public Page<FootballClub> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<FootballClub> findAllByIdAndCountry(List<Long> longs, CountryCode country) {
        return repository.findAllByIdInAndCountryIn(longs, country);
    }

    public List<FootballClub> findByIdNotInAndCountryIn(List<Long> footballClubIds, CountryCode country, int amount) {
        var pageable = PageRequest.of(0, amount);
        return repository.findByIdNotInAndCountryIn(footballClubIds, country, pageable);
    }

    public Page<FootballClub> findByMultipleCriteria(SearchFootballClubSearchCriteria criteria, Pageable pageable) {
        Query query = new Query().with(pageable);
        if (StringUtils.isNotEmpty(criteria.name())) {
            query.addCriteria(Criteria.where("name").regex(".*" + criteria.name() + ".*", "i"));
        }
        if (criteria.country() != null) {
            query.addCriteria(Criteria.where("country").is(criteria.country()));
        }
        if (StringUtils.isNotEmpty(criteria.stadiumName())) {
            query.addCriteria(Criteria.where("stadiumName").regex(".*" + criteria.stadiumName() + ".*", "i"));
        }
        List<FootballClub> clubs = template.find(query, FootballClub.class);
        long total = template.count(Query.of(query).limit(-1).skip(-1), FootballClub.class);
        return new PageImpl<>(clubs, pageable, total);
    }

    public Page<FootballClub> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<FootballClub> findByStadiumNameContainingIgnoreCase(String stadium, Pageable pageable) {
        return repository.findByStadiumNameContainingIgnoreCase(stadium, pageable);
    }

    public Page<FootballClub> findByCountry(CountryCode country, Pageable pageable) {
        return repository.findByCountry(country, pageable);
    }
}
