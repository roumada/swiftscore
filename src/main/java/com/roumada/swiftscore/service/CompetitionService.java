package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.competition.CompetitionCreator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.criteria.SearchCompetitionCriteria;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequest;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.persistence.datalayer.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.datalayer.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.datalayer.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.datalayer.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.sequence.PrimarySequenceService;
import com.roumada.swiftscore.util.Messages;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
                    String warnMsg = Messages.COMPETITION_NOT_FOUND.format(id);
                    log.warn(warnMsg);
                    return Either.left(warnMsg);
                });
    }

    public Either<String, Competition> generateAndSave(CreateCompetitionRequest dto) {
        return generateAndSave(dto, Collections.emptyList());
    }

    public Either<String, Competition> generateAndSave(CreateCompetitionRequest dto, List<Long> excludedClubIds) {
        if (dto.participantsAmount() % 2 == 1) {
            var errorMsg = Messages.COMPETITION_CANNOT_GENERATE_CLUB_AMT_MUST_BE_EVEN.format();
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var findClubsResult = findClubs(dto, excludedClubIds);
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

    public Either<ErrorResponse, Long> generateFromConcluded(Competition comp) {
        var creationResult = CompetitionCreator.createFromConcluded(comp);
        if (creationResult.isLeft()) {
            return Either.left(new ErrorResponse(List.of(creationResult.getLeft())));
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

        return Either.right(competitionDataLayer.save(competition).getId());
    }

    public Either<String, List<CompetitionRound>> simulate(Competition competition, int times) {
        if (competition.isFullySimulated()) {
            String errorMsg = Messages.COMPETITION_CANNOT_SIMULATE.format(competition.getId());
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        times = adjustTimesToSimulate(competition, times);
        List<CompetitionRound> simulatedRounds = new ArrayList<>();
        do {
            simulateCurrentRound(competition);
            CompetitionRound currRound = competition.currentRound();
            saveRound(currRound);
            simulatedRounds.add(currRound);
            competition.incrementCurrentRoundNumber();
            times--;
        } while (times > 0);

        competitionDataLayer.save(competition);
        return Either.right(simulatedRounds);
    }

    private void simulateCurrentRound(Competition competition) {
        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withValues(competition.getSimulationParameters()));
        roundSimulator.simulate(competition.currentRound());
        log.info(Messages.COMPETITION_SIMULATED.format(competition.getId()));
    }

    private Either<String, List<FootballClub>> findClubs(CreateCompetitionRequest dto, List<Long> excludedClubIds) {
        List<FootballClub> clubs = new ArrayList<>();

        if (dto.participantIds() != null) {
            clubs = new ArrayList<>(footballClubDataLayer.findAllByIdAndCountry(dto.participantIds(), dto.country()));

            if (clubs.size() != dto.participantIds().size()) {
                return Either.left(Messages.FOOTBALL_CLUBS_COULDNT_RETRIEVE_ALL_FROM_IDS.format());
            }

            if (dto.participants() <= dto.participantIds().size()) {
                log.info(Messages.FOOTBALL_CLUBS_PARTICIPANTS_AMT_LOWER_THAN_FCIDS
                        .format(dto.participants(), dto.participantIds().size()));
                return Either.right(clubs);
            }
        }

        List<Long> excludedTotalIds = new ArrayList<>();
        excludedTotalIds.addAll(excludedClubIds);
        excludedTotalIds.addAll((dto.participantIds()));

        clubs.addAll(footballClubDataLayer
                .findByIdNotInAndCountryIn(excludedTotalIds, dto.country(), dto.participants() - dto.participantIds().size()));
        return clubs.size() == dto.participantsAmount() ?
                Either.right(clubs) :
                Either.left(Messages.FOOTBALL_CLUBS_NOT_ENOUGH_CLUBS_FROM_COUNTRY.format());
    }

    public Either<String, Competition> update(long id, UpdateCompetitionRequest dto) {
        var findResult = competitionDataLayer.findCompetitionById(id);
        if (findResult.isEmpty()) {
            String warnMsg = Messages.COMPETITION_NOT_FOUND.format(id);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }

        Competition competition = findResult.get();

        if (dto.relegationSpots() != null) {
            if (competition.getParticipants().size() - 1 <= dto.relegationSpots()) {
                String warnMsg = Messages.COMPETITION_INVALID_RELEGATION_SPOTS_AMOUNT
                        .format(dto.relegationSpots(), competition.getParticipants().size());
                log.warn(warnMsg);
                return Either.left(warnMsg);
            } else competition.setRelegationSpots(dto.relegationSpots());
        }

        if (dto.name() != null) competition.setName(dto.name());
        if (dto.country() != null) competition.setCountry(dto.country());
        if (dto.simulationParameters() != null) {
            var variance = ObjectUtils.defaultIfNull(dto.simulationParameters().variance(),
                    competition.getSimulationParameters().variance());

            var sddt = ObjectUtils.defaultIfNull(dto.simulationParameters().scoreDifferenceDrawTrigger(),
                    competition.getSimulationParameters().scoreDifferenceDrawTrigger());

            var dtc = ObjectUtils.defaultIfNull(dto.simulationParameters().drawTriggerChance(),
                    competition.getSimulationParameters().drawTriggerChance());

            competition.setSimulationParameters(new SimulationParameters(variance, sddt, dtc));
        }
        return Either.right(competitionDataLayer.save(competition));
    }

    private void saveRound(CompetitionRound currRound) {
        footballMatchDataLayer.saveAll(currRound.getMatches());
        competitionRoundDataLayer.save(currRound);
    }

    public void delete(long id) {
        footballMatchDataLayer.deleteByCompetitionId(id);
        competitionRoundDataLayer.deleteByCompetitionId(id);
        competitionDataLayer.delete(id);
    }

    private int adjustTimesToSimulate(Competition competition, int times) {
        if (competition.getRounds().size() - competition.getLastSimulatedRound() < times) {
            log.info(Messages.COMPETITION_SIMULATED_UNTIL_END.format(
                    times, competition.getRounds().size() - competition.getLastSimulatedRound()));
            return competition.getRounds().size() - competition.getLastSimulatedRound();
        }
        return times;
    }

    public Page<Competition> search(SearchCompetitionCriteria criteria, Pageable pageable) {
        if (criteria.hasNoCriteria()) return competitionDataLayer.findAllCompetitions(pageable);
        if (criteria.hasOneCriteria()) return searchWithSingleCriteria(criteria, pageable);

        return competitionDataLayer.findByMultipleCriteria(criteria, pageable);
    }

    private Page<Competition> searchWithSingleCriteria(SearchCompetitionCriteria criteria, Pageable pageable) {
        return switch (criteria.getSingleCriteriaType()) {
            case NAME -> competitionDataLayer.findByName(criteria.name(), pageable);
            case COUNTRY -> competitionDataLayer.findByCountry(criteria.country(), pageable);
            case SEASON -> competitionDataLayer.findBySeason(criteria.season(), pageable);
            default -> Page.empty();
        };
    }



}
