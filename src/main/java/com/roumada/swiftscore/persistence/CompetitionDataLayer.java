package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class CompetitionDataLayer {

    private final CompetitionRepository competitionRepository;
    private final CompetitionRoundRepository competitionRoundRepository;
    private final FootballClubRepository footballClubRepository;
    private final FootballMatchDataLayer footballMatchDataLayer;

    public Either<String, Competition> generateAndSave(CompetitionRequestDTO dto) {
        var footballClubs = new ArrayList<FootballClub>();
        for (Long id : dto.participantIds()) {
            footballClubs.add(footballClubRepository.findById(id).orElse(null));
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
                    saveRounds(rounds);

                    var competition = Competition.builder()
                            .variance(dto.variance())
                            .participants(footballClubs)
                            .rounds(rounds)
                            .build();
                    competitionRepository.save(competition);
                    return Either.right(competition);
                });
    }


    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> findAllComps() {
        return competitionRepository.findAll();
    }

    public Competition saveCompetition(Competition competition) {
        var saved = competitionRepository.save(competition);
        log.info("Competition with ID [{}] saved.", saved.getId());
        return saved;
    }

    private void saveRounds(List<CompetitionRound> rounds) {
        for (CompetitionRound round : rounds) {
            saveCompetitionRound(round);
        }
    }

    public CompetitionRound saveCompetitionRound(CompetitionRound round) {
        for (FootballMatch match : round.getMatches()) {
            saveMatch(match);
        }
        var saved = competitionRoundRepository.save(round);
        log.info("Competition round with ID [{}] saved", saved.getId());
        return saved;
    }

    private void saveMatch(FootballMatch match) {
        var matchId = footballMatchDataLayer.saveMatch(match).getId();
        log.info("Match with ID [{}] saved.", matchId);
    }
}
