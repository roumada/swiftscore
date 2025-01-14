package com.roumada.swiftscore.logic.data;

import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
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

    public Either<String, Competition> generateAndSave(CompetitionRequestDTO dto) {
        var footballClubs = new ArrayList<FootballClub>();
        for (Long id : dto.participantIds()) {
            footballClubs.add(fcDataLayer.findById(id).orElse(null));
        }

        if (footballClubs.contains(null)) {
            var error = "Failed to generate competition - failed to retrieve at least one club from the database.";
            log.error(error);
            return Either.left(error);
        }
        var generationResult = CompetitionRoundsGenerator.generate(footballClubs);
        return generationResult.fold(
                Either::left,
                rounds -> {
                    var competition = Competition.builder()
                            .simulatorValues(dto.simulatorValues())
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
            String error =
                    "Cannot simulate competition with [%s] and current round [%s]. All of the competition's rounds have been simulated. Unable to simulate further"
                            .formatted(competition.getId(), competition.getCurrentRoundNumber());
            log.error(error);
            return Either.left(error);
        }

        var compSimulated = simulateCurrentRound(competition);
        var currRound = competition.currentRound();
        persistChanges(compSimulated);
        return Either.right(currRound);
    }

    private void persistChanges(Competition compSimulated) {
        competitionDataLayer.saveCompetitionRound(compSimulated.currentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.saveCompetition(compSimulated);
    }

    private Competition simulateCurrentRound(Competition competition) {
        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withVariance(competition.getSimulatorValues().variance()));
        roundSimulator.simulate(competition.currentRound());
        log.info("Competition with id [{}] simulated.", competition.getId());
        return competition;
    }

    public Either<String, Competition> findCompetitionById(Long id) {
        var optionalCompetition = competitionDataLayer.findCompetitionById(id);
        return optionalCompetition
                .map(Either::<String, Competition>right)
                .orElseGet(() -> {
                    String error = "Competition with ID [%s] not found.".formatted(id);
                    log.info(error);
                    return Either.left(error);
                });
    }

    public List<Competition> findAllCompetitions() {
        return competitionDataLayer.findAllCompetitions();
    }
}
