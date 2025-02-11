package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteriaDTO;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FootballClubService {

    private final FootballClubRepository repository;
    private final MongoTemplate template;

    public Either<String, FootballClub> findById(long id) {
        var findResult = repository.findById(id);
        if (findResult.isEmpty()) {
            String errorMsg = "Unable to find football club with given id [%s]".formatted(id);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        } else return Either.right(findResult.get());
    }

    public List<FootballClub> findAllByIds(Iterable<Long> ids) {
        return repository.findAllById(ids);
    }

    public FootballClub save(CreateFootballClubRequestDTO dto) {
        return repository.save(FootballClubMapper.INSTANCE.requestToObject(dto));
    }

    public Either<String, FootballClub> update(Long id, CreateFootballClubRequestDTO dto) {
        var findResult = repository.findById(id);
        if (findResult.isEmpty()) {
            String errorMsg = "Unable to find football club with given id [%s]".formatted(id);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        if (dto.victoryChance() != 0.0 && (dto.victoryChance() < 0 || dto.victoryChance() > 1)) {
            String errorMsg = "Victory chance cannot be lower than 0 or higher than 1";
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        var footballClub = findResult.get();
        updateFields(dto, footballClub);

        return Either.right(repository.save(footballClub));
    }

    private void updateFields(CreateFootballClubRequestDTO dto, FootballClub footballClub) {
        if (dto.name() != null) {
            footballClub.setName(dto.name());
        }
        if (dto.country() != null) {
            footballClub.setCountry(dto.country());
        }
        if (dto.stadiumName() != null) {
            footballClub.setStadiumName(dto.stadiumName());
        }
        if (dto.victoryChance() != 0.0) {
            footballClub.setVictoryChance(dto.victoryChance());
        }
    }

    public Page<FootballClub> searchClubs(SearchFootballClubSearchCriteriaDTO criteria, Pageable pageable) {
        if (criteria.hasNoCriteria())
            return repository.findAll(pageable);
        if (criteria.hasOneCriteria())
            return searchWithSingleCriteria(criteria, pageable);

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

    private Page<FootballClub> searchWithSingleCriteria(SearchFootballClubSearchCriteriaDTO criteria, Pageable pageable) {
        return switch (criteria.getSingleCriteriaType()) {
            case NAME -> repository.findByNameContainingIgnoreCase(criteria.name(), pageable);
            case STADIUM_NAME -> repository.findByStadiumNameContainingIgnoreCase(criteria.stadiumName(), pageable);
            case COUNTRY -> repository.findByCountry(criteria.country(), pageable);
            case NONE -> Page.empty();
        };
    }
}
