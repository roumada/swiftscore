package com.roumada.swiftscore.persistence.datalayer;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.dto.criteria.SearchCompetitionCriteria;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class CompetitionDataLayer {

    private final CompetitionRepository competitionRepository;
    private final MongoTemplate template;

    public Competition save(Competition competition) {
        var saved = competitionRepository.save(competition);
        log.info("Competition with ID [{}] saved.", saved.getId());
        return saved;
    }

    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public Page<Competition> findAllCompetitions(Pageable pageable) {
        return competitionRepository.findAll(pageable);
    }

    public void delete(long id) {
        competitionRepository.deleteById(id);
        log.info("Competition with ID [{}] deleted.", id);
    }

    public PageImpl<Competition> findByMultipleCriteria(SearchCompetitionCriteria criteria, Pageable pageable) {
        Query query = new Query().with(pageable);
        if (StringUtils.isNotEmpty(criteria.name())) {
            query.addCriteria(Criteria.where("name").regex(".*" + criteria.name() + ".*", "i"));
        }
        if (criteria.country() != null) {
            query.addCriteria(Criteria.where("country").is(criteria.country()));
        }
        if (StringUtils.isNotEmpty(criteria.season())) {
            query.addCriteria(Criteria.where("season").is(criteria.season()));
        }
        List<Competition> competitions = template.find(query, Competition.class);
        long total = template.count(Query.of(query).limit(-1).skip(-1), Competition.class);
        return new PageImpl<>(competitions, pageable, total);
    }

    public Page<Competition> findBySeason(String season, Pageable pageable) {
        return competitionRepository.findBySeason(season, pageable);
    }

    public Page<Competition> findByName(String name, Pageable pageable) {
        return competitionRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Competition> findByCountry(CountryCode country, Pageable pageable) {
        return competitionRepository.findByCountry(country, pageable);
    }
}
