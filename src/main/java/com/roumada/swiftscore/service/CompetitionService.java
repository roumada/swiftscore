package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.creator.CompetitionCreator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.criteria.SearchCompetitionCriteriaDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Either<String, Competition> generateAndSave(CreateCompetitionRequestDTO dto) {
        if (dto.participantsAmount() % 2 == 1) {
            var errorMsg = "Failed to generate competition - the amount of clubs participating must be even.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var findClubsResult = findClubs(dto);

        if (findClubsResult.isLeft()) {
            log.error(findClubsResult.getLeft());
            return Either.left(findClubsResult.getLeft());
        }

        var creationResult = CompetitionCreator.createFromRequest(dto, findClubsResult.get());
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


    public Either<String, List<CompetitionRound>> simulate(Competition competition, int times) {
        if (competition.isFullySimulated()) {
            String errorMsg =
                    "Cannot simulate competition [%s] further".formatted(competition.getId());
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        times = adjustTimesToSimulate(competition, times);

        List<CompetitionRound> simulatedRounds = new ArrayList<>();
        do {
            Competition compSimulated = simulateCurrentRound(competition);
            CompetitionRound currRound = competition.currentRound();
            persistChanges(compSimulated);
            simulatedRounds.add(currRound);
            times--;
        } while (times > 0);

        return Either.right(simulatedRounds);
    }


    private Competition simulateCurrentRound(Competition competition) {
        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withValues(competition.getSimulationValues()));
        roundSimulator.simulate(competition.currentRound());
        log.info("Competition with id [{}] simulated.", competition.getId());
        return competition;
    }

    private Either<String, List<FootballClub>> findClubs(CreateCompetitionRequestDTO dto) {
        if (dto.participantsAmount() == 0) {
            return Either.left("Neither fillToParticipants nor footballClubIDs have been set");
        }

        List<FootballClub> clubs = new ArrayList<>();

        if (dto.participantIds() != null) {
            clubs = new ArrayList<>(footballClubDataLayer.findAllByIdAndCountry(dto.participantIds(), dto.country()));

            if (clubs.size() != dto.participantIds().size()) {
                return Either.left("Couldn't retrieve all clubs for given IDs and country");
            }

            if (dto.fillToParticipants() <= dto.participantIds().size()) {
                log.info("FillToParticipants parameter [{}] lower than amount of club IDs provided ([{}]). Returning clubs with denoted club IDs only.",
                        dto.fillToParticipants(), dto.participantIds().size());
                return Either.right(clubs);
            }
        }

        clubs.addAll(footballClubDataLayer
                .findByIdNotInAndCountryIn(dto.participantIds(), dto.country(), dto.fillToParticipants() - dto.participantIds().size()));
        return clubs.size() == dto.participantsAmount() ?
                Either.right(clubs) :
                Either.left("Couldn't find enough clubs from given country to fill in the league");
    }

    private void persistChanges(Competition compSimulated) {
        footballMatchDataLayer.saveAll(compSimulated.currentRound().getMatches());
        competitionRoundDataLayer.save(compSimulated.currentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.save(compSimulated);
    }

    public Either<String, Competition> update(long id, UpdateCompetitionRequestDTO dto) {
        var findResult = competitionDataLayer.findCompetitionById(id);
        if (findResult.isEmpty()) {
            String warnMsg = "Competition with ID [%s] not found.".formatted(id);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }

        Competition competition = findResult.get();

        if (dto.name() != null) competition.setName(dto.name());
        if (dto.country() != null) competition.setCountry(dto.country());
        if (dto.simulationValues() != null) {
            var variance = ObjectUtils.defaultIfNull(dto.simulationValues().variance(),
                    competition.getSimulationValues().variance());

            var sddt = ObjectUtils.defaultIfNull(dto.simulationValues().scoreDifferenceDrawTrigger(),
                    competition.getSimulationValues().scoreDifferenceDrawTrigger());

            var dtc = ObjectUtils.defaultIfNull(dto.simulationValues().drawTriggerChance(),
                    competition.getSimulationValues().drawTriggerChance());

            competition.setSimulationValues(new SimulationValues(variance, sddt, dtc));
        }
        return Either.right(competitionDataLayer.save(competition));
    }

    public void delete(long id) {
        footballMatchDataLayer.deleteByCompetitionId(id);
        competitionRoundDataLayer.deleteByCompetitionId(id);
        competitionDataLayer.delete(id);
    }

    private int adjustTimesToSimulate(Competition competition, int times) {
        if (competition.getRounds().size() - competition.getLastSimulatedRound() < times) {
            log.info("Attempting to simulate a competition [{}] times while the competition has only [{}] rounds left. Simulating the entire competition",
                    times, competition.getRounds().size() - competition.getLastSimulatedRound());
            return competition.getRounds().size() - competition.getLastSimulatedRound();
        }
        return times;
    }

    public Page<Competition> search(SearchCompetitionCriteriaDTO criteria, Pageable pageable) {
        if(criteria.hasNoCriteria()) return competitionDataLayer.findAllCompetitions(pageable);
        if(criteria.hasOneCriteria()) return searchWithSingleCriteria(criteria, pageable);
        return competitionDataLayer.findByNameContainingIgnoreCaseAndCountry(criteria.name(), criteria.country(), pageable);
    }

    private Page<Competition> searchWithSingleCriteria(SearchCompetitionCriteriaDTO criteria, Pageable pageable) {
        return switch(criteria.getSingleCriteriaType()){
            case NAME -> competitionDataLayer.findByNameContaining(criteria.name(), pageable);
            case COUNTRY -> competitionDataLayer.findByCountry(criteria.country(), pageable);
            default -> Page.empty();
        };
    }
}
