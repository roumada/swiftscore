package com.roumada.swiftscore.logic.creator;

import com.roumada.swiftscore.logic.CompetitionDatesProvider;
import com.roumada.swiftscore.model.dto.request.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CompetitionCreator {

    private final FootballClubDataLayer footballClubDataLayer;

    private static CompetitionDatesProvider createProvider(CompetitionRequestDTO dto) {
        var provider = new CompetitionDatesProvider(
                LocalDate.parse(dto.startDate()),
                LocalDate.parse(dto.endDate()),
                dto.participantIds().size());
        log.info("Created date provider with start date [{}] and step [{}]", provider.getStart(), provider.getStep());
        return provider;
    }

    public Either<String, Competition> createFromRequest(CompetitionRequestDTO dto) {
        var footballClubs = footballClubDataLayer.findAllById(dto.participantIds());

        if (footballClubs.size() != dto.participantIds().size()) {
            var errorMsg = "Failed to generate competition - failed to retrieve at least one club from the database.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        var generationResult = CompetitionRoundsCreator.create(footballClubs);
        if (generationResult.isLeft()) {
            return Either.left(generationResult.getLeft());
        }

        var rounds = generationResult.get();
        CompetitionDatesProvider provider = createProvider(dto);
        setDatesForMatchesInRounds(provider, rounds);
        // initial save to get competition ID
        return Either.right(Competition.builder()
                .name(dto.name())
                .type(dto.type())
                .country(dto.country())
                .startDate(LocalDate.parse(dto.startDate()))
                .endDate(LocalDate.parse(dto.endDate()))
                .simulationValues(dto.simulationValues())
                .participants(footballClubs)
                .rounds(rounds)
                .build());
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
}
