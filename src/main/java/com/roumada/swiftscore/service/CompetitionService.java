package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.creator.CompetitionCreator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.CompetitionUpdateRequestDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionRoundMapper;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionService {

    private final PrimarySequenceService sequenceService;
    private final CompetitionDataLayer competitionDataLayer;
    private final CompetitionRoundDataLayer competitionRoundDataLayer;
    private final FootballMatchDataLayer footballMatchDataLayer;
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
        if(dto.participantIds().size() % 2 == 1){
            var errorMsg = "Failed to generate competition - the amount of clubs participating must be even.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var footballClubs = footballClubDataLayer.findAllById(dto.participantIds());

        if (footballClubs.size() != dto.participantIds().size()) {
            var errorMsg = "Failed to generate competition - failed to retrieve at least one club from the database.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var creationResult = CompetitionCreator.createFromRequest(dto, footballClubs);
        if (creationResult.isLeft()) {
            return Either.left(creationResult.getLeft());
        }
        Long compId = sequenceService.getNextValue();
        Competition competition = creationResult.get();
        competition.setId(compId);

        for (CompetitionRound round : competition.getRounds()) {
            Long roundId = sequenceService.getNextValue();
            for (FootballMatch match : round.getMatches()) {
                match.setCompetitionId(compId);
                match.setCompetitionRoundId(roundId);
                footballMatchDataLayer.save(match);
            }
            round.setCompetitionId(compId);
            round.setId(roundId);
            competitionRoundDataLayer.save(round);
        }
        competitionDataLayer.save(competition);

        return Either.right(competition);
    }


    public Either<String, CompetitionRound> simulateRound(Competition competition) {
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

        return Either.right(currRound);
    }

    private Competition simulateCurrentRound(Competition competition) {
        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withValues(competition.getSimulationValues()));
        roundSimulator.simulate(competition.currentRound());
        log.info("Competition with id [{}] simulated.", competition.getId());
        return competition;
    }

    private void persistChanges(Competition compSimulated) {
        footballMatchDataLayer.saveAll(compSimulated.currentRound().getMatches());
        competitionRoundDataLayer.save(compSimulated.currentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.save(compSimulated);
    }

    public Either<String, Competition> update(long id, CompetitionUpdateRequestDTO dto) {
        var findResult = competitionDataLayer.findCompetitionById(id);
        if (findResult.isEmpty()) {
            String warnMsg = "Competition with ID [%s] not found.".formatted(id);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }

        Competition competition = findResult.get();

        if (dto.name() != null) competition.setName(dto.name());
        if (dto.country() != null) competition.setCountry(dto.country());
        if (dto.simulationValues() != null) competition.setSimulationValues(dto.simulationValues());

        return Either.right(competitionDataLayer.save(competition));
    }

    public void delete(long id) {
        footballMatchDataLayer.deleteByCompetitionId(id);
        competitionRoundDataLayer.deleteByCompetitionId(id);
        competitionDataLayer.delete(id);
    }
}
