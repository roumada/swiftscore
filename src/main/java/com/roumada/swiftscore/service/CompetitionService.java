package com.roumada.swiftscore.service;

import com.roumada.swiftscore.logic.CompetitionDatesProvider;
import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionRoundMapper;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;
    private final CompetitionRoundDataLayer competitionRoundDataLayer;
    private final FootballMatchDataLayer footballMatchDataLayer;
    private final FootballClubDataLayer footballClubDataLayer;
    private final Validator validator;

    private static CompetitionDatesProvider createProvider(CompetitionRequestDTO dto) {
        var provider = new CompetitionDatesProvider(
                LocalDate.parse(dto.startDate()),
                LocalDate.parse(dto.endDate()),
                dto.participantIds().size());
        log.info("Created date provider with start date [{}] and step [{}]", provider.getStart(), provider.getStep());
        return provider;
    }

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
            footballClubs.add(footballClubDataLayer.findById(id).orElse(null));
        }

        if (footballClubs.contains(null)) {
            var errorMsg = "Failed to generate competition - failed to retrieve at least one club from the database.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }
        var generationResult = CompetitionRoundsGenerator.generate(footballClubs);
        if (generationResult.isLeft()) {
            return Either.left(generationResult.getLeft());
        }

        var rounds = generationResult.get();
        CompetitionDatesProvider provider = createProvider(dto);
        setDatesForMatchesInRounds(provider, rounds);
        // initial save to get competition ID
        var competition = competitionDataLayer.saveCompetition(Competition.builder()
                .name(dto.name())
                .type(dto.type())
                .country(dto.country())
                .startDate(LocalDate.parse(dto.startDate()))
                .endDate(LocalDate.parse(dto.endDate()))
                .simulationValues(dto.simulationValues())
                .participants(footballClubs)
                .rounds(Collections.emptyList())
                .build());

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

    private void setDatesForMatchesInRounds(CompetitionDatesProvider provider, List<CompetitionRound> rounds) {
        for (CompetitionRound round : rounds) {
            LocalDate date = provider.next();
            for (FootballMatch match : round.getMatches()) {
                var datetime = LocalDateTime.of(date, LocalTime.of(21, 0));
                match.setDate(datetime);
            }
        }
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
