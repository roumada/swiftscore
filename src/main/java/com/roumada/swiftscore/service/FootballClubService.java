package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteria;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.persistence.datalayer.FootballClubDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FootballClubService {

    private final FootballClubDataLayer dataLayer;

    public Either<String, FootballClub> findById(long id) {
        var findResult = dataLayer.findById(id);
        if (findResult.isEmpty()) {
            String errorMsg = "Unable to find football club with given id [%s]".formatted(id);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        } else return Either.right(findResult.get());
    }

    public List<FootballClub> findAllById(List<Long> ids){
        return dataLayer.findAllById(ids);
    }

    public FootballClub save(CreateFootballClubRequest dto) {
        return dataLayer.save(FootballClubMapper.INSTANCE.requestToObject(dto));
    }

    public Either<String, FootballClub> update(Long id, CreateFootballClubRequest dto) {
        var findResult = dataLayer.findById(id);
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

        return Either.right(dataLayer.save(footballClub));
    }

    private void updateFields(CreateFootballClubRequest dto, FootballClub footballClub) {
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

    public Page<FootballClub> searchClubs(SearchFootballClubSearchCriteria criteria, Pageable pageable) {
        if (criteria.hasNoCriteria())
            return dataLayer.findAll(pageable);
        if (criteria.hasOneCriteria())
            return searchWithSingleCriteria(criteria, pageable);

        return dataLayer.findByMultipleCriteria(criteria, pageable);
    }

    private Page<FootballClub> searchWithSingleCriteria(SearchFootballClubSearchCriteria criteria, Pageable pageable) {
        return switch (criteria.getSingleCriteriaType()) {
            case NAME -> dataLayer.findByNameContainingIgnoreCase(criteria.name(), pageable);
            case STADIUM_NAME -> dataLayer.findByStadiumNameContainingIgnoreCase(criteria.stadiumName(), pageable);
            case COUNTRY -> dataLayer.findByCountry(criteria.country(), pageable);
            default -> Page.empty();
        };
    }
}
