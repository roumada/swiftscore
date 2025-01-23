package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.creator.CompetitionCreator;
import com.roumada.swiftscore.logic.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionRoundMapper;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;
    private final CompetitionRoundDataLayer competitionRoundDataLayer;
    private final FootballMatchDataLayer footballMatchDataLayer;
    private final CompetitionCreator competitionCreator;
    private final FootballClubDataLayer footballClubDataLayer;
    private final Validator validator;

    public Either<String, Competition> findCompetitionById(Long id) {
        var optionalCompetition = competitionDataLayer.findCompetitionById(id);
        return optionalCompetition
                .map(Either::<String, Competition>right)
                .orElseGet(() -> {
                    String warnMsg = "Competition with ID [%s] not found.".formatted(id);
                    log.warn(warnMsg);
                    return Either.left(warnMsg);
                });
    }

    public List<Competition> findAllCompetitions() {
        return competitionDataLayer.findAllCompetitions();
    }

    public Either<String, Competition> generateAndSave(CompetitionRequestDTO dto) {
        var creationResult = competitionCreator.createFromRequest(dto);
        if (creationResult.isLeft()) {
            return Either.left(creationResult.getLeft());
        }
        Competition competition = creationResult.get();
        var rounds = competition.getRounds();
        competition.setRounds(Collections.emptyList());
        competitionDataLayer.saveCompetition(competition);

        // set comp ID to all rounds
        for (CompetitionRound round : rounds) {
            round.setCompetitionId(competition.getId());
        }

        // save rounds with comp ID
        var savedRounds = competitionDataLayer.saveRounds(rounds);
        competition.setRounds(savedRounds);
        // save comp with underlying rounds with comp ID
        competition = competitionDataLayer.saveCompetition(competition);
        // save matches within rounds with comp IDs
        competitionDataLayer.deepSaveCompetitionMatchesWithCompIds(competition);

        return Either.right(competition);
    }


    public Either<String, CompetitionRoundResponseDTO> simulateRound(Competition competition) {
        if (!competition.canSimulate()) {
            String errorMsg =
                    "Cannot simulate competition with [%s] and current round [%s]. All of the competition's rounds have been simulated. Unable to simulate further"
                            .formatted(competition.getId(), competition.getCurrentRoundNumber());
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var compSimulated = simulateCurrentRound(competition);
        var currRound = competition.currentRound();
        persistChanges(compSimulated);

        return Either.right(CompetitionRoundMapper.INSTANCE.roundToResponseDTO(currRound));
    }

    private Competition simulateCurrentRound(Competition competition) {
        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withValues(competition.getSimulationValues()));
        roundSimulator.simulate(competition.currentRound());
        log.info("Competition with id [{}] simulated.", competition.getId());
        return competition;
    }

    private void persistChanges(Competition compSimulated) {
        competitionDataLayer.saveCompetitionRound(compSimulated.currentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.saveCompetition(compSimulated);
    }

    public Either<String, Competition> update(long id, CompetitionRequestDTO dto) {
        var findResult = competitionDataLayer.findCompetitionById(id);
        if (findResult.isEmpty()) {
            String warnMsg = "Competition with ID [%s] not found.".formatted(id);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }

        Competition competition = findResult.get();

        if (dto.name() != null) competition.setName(dto.name());
        if (dto.country() != null) competition.setCountry(dto.country());
        if (dto.type() != null) competition.setType(dto.type());
        if (dto.simulationValues() != null) {
            var violations = validator.validate(dto.simulationValues());
            if (violations.isEmpty()) competition.setSimulationValues(dto.simulationValues());
        }

        return Either.right(competitionDataLayer.saveCompetition(competition));
    }

    public void delete(long id) {
        footballMatchDataLayer.deleteByCompetitionId(id);
        competitionRoundDataLayer.deleteByCompetitionId(id);
        competitionDataLayer.delete(id);
    }
}
