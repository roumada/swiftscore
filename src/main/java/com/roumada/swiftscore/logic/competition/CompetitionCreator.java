package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
public class CompetitionCreator {

    private CompetitionCreator() {
    }

    public static Either<String, Competition> createFromRequest(CreateCompetitionRequest dto, List<FootballClub> footballClubs) {
        var generationResult = CompetitionRoundsCreator.create(footballClubs);
        if (generationResult.isLeft()) {
            return Either.left(generationResult.getLeft());
        }

        var rounds = generationResult.get();
        CompetitionDatesProvider provider = createProvider(dto);
        setDatesForMatchesInRounds(provider, rounds);
        return Either.right(Competition.builder()
                .name(dto.name())
                .country(dto.country())
                .startDate(LocalDate.parse(dto.startDate()))
                .endDate(LocalDate.parse(dto.endDate()))
                .simulationParameters(dto.simulationParameters())
                .relegationSpots(dto.competitionParameters().relegationSpots())
                .participants(footballClubs)
                .rounds(rounds)
                .build());
    }

    public static Either<String, Competition> createFromConcluded(Competition competition) {
        var generationResult = CompetitionRoundsCreator.create(competition.getParticipants());
        if (generationResult.isLeft()) {
            return Either.left(generationResult.getLeft());
        }

        var rounds = generationResult.get();
        CompetitionDatesProvider provider = createProvider(competition);
        setDatesForMatchesInRounds(provider, rounds);
        return Either.right(Competition.builder()
                .name(competition.getName())
                .country(competition.getCountry())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .simulationParameters(competition.getSimulationParameters())
                .relegationSpots(competition.getRelegationSpots())
                .participants(competition.getParticipants())
                .rounds(rounds)
                .build());
    }

    private static CompetitionDatesProvider createProvider(CreateCompetitionRequest dto) {
        var provider = new CompetitionDatesProvider(
                LocalDate.parse(dto.startDate()),
                LocalDate.parse(dto.endDate()),
                dto.participantsAmount());
        log.info("Created date provider with start date [{}] and step [{}]", provider.getStart(), provider.getStep());
        return provider;
    }

    private static CompetitionDatesProvider createProvider(Competition competition) {
        var provider = new CompetitionDatesProvider(competition.getStartDate(),
                competition.getEndDate(),
                competition.getParticipants().size());
        log.info("Created date provider with start date [{}] and step [{}]", provider.getStart(), provider.getStep());
        return provider;
    }

    private static void setDatesForMatchesInRounds(CompetitionDatesProvider provider, List<CompetitionRound> rounds) {
        for (CompetitionRound round : rounds) {
            LocalDate date = provider.next();
            for (FootballMatch match : round.getMatches()) {
                var datetime = LocalDateTime.of(date, LocalTime.of(21, 0));
                match.setDate(datetime);
            }
        }
    }
}
