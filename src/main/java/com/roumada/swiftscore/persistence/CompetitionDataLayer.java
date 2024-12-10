package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.logic.competition.schedule.CompetitionRoundsGenerator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
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

    public Competition generateAndSave(List<Long> participantIds) {
        var footballClubs = new ArrayList<FootballClub>();
        for (Long id : participantIds) {
            footballClubs.add(footballClubRepository.findById(id).orElse(null));
        }

        if (footballClubs.contains(null)) {
            log.error("Failed to generate competition - at least one of the club IDs was invalid.");
            return null;
        }

        var rounds = CompetitionRoundsGenerator.generate(footballClubs);
        saveRounds(rounds);

        var competition = new Competition();
        competition.setParticipants(footballClubs);
        competition.setRounds(rounds);
        return competitionRepository.save(competition);
    }

    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> findAllComps() {
        return competitionRepository.findAll();
    }

    private void saveRounds(List<CompetitionRound> rounds) {
        for(CompetitionRound round : rounds){
            for(FootballMatch match : round.getMatches()) {
                footballMatchDataLayer.saveStatistics(match.getHomeSideStatistics());
                footballMatchDataLayer.saveStatistics(match.getAwaySideStatistics());
                footballMatchDataLayer.saveMatch(match);
            }
        }
        competitionRoundRepository.saveAll(rounds);
    }
}
