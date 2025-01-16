package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;
    private final FootballClubDataLayer fcDataLayer;

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
        var footballClubs = new ArrayList<FootballClub>();
        for (Long id : dto.participantIds()) {
            footballClubs.add(fcDataLayer.findById(id).orElse(null));
        }

        if (footballClubs.contains(null)) {
            var errorMsg = "Failed to generate competition - failed to retrieve at least one club from the database.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }
        var generationResult = CompetitionRoundsGenerator.generate(footballClubs);
        return generationResult.fold(
                Either::left,
                rounds -> {
                    var competition = Competition.builder()
                            .simulationValues(dto.simulationValues())
                            .participants(footballClubs)
                            .rounds(Collections.emptyList())
                            .build();
                    competition = competitionDataLayer.saveCompetition(competition);

                    for (CompetitionRound round : rounds) {
                        round.setCompetitionId(competition.getId());
                    }
                    var savedRounds = competitionDataLayer.saveRounds(rounds);
                    competition.setRounds(savedRounds);
                    competition = competitionDataLayer.saveCompetition(competition);
                    competitionDataLayer.deepSaveCompetitionMatchesWithCompIds(competition);

                    return Either.right(competition);
                });
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
        competitionDataLayer.saveCompetitionRound(compSimulated.currentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.saveCompetition(compSimulated);
    }
}
