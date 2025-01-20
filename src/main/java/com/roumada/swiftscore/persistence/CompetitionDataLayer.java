package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
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
    private final FootballMatchDataLayer footballMatchDataLayer;

    public void deepSaveCompetitionMatchesWithCompIds(Competition competition) {
        for (CompetitionRound round : competition.getRounds()) {
            for (FootballMatch match : round.getMatches()) {
                saveMatchesWithCompIds(match, round.getId(), round.getCompetitionId());
            }
        }
    }

    public Competition saveCompetition(Competition competition) {
        var saved = competitionRepository.save(competition);
        log.debug("Competition with ID [{}] saved.", saved.getId());
        return saved;
    }

    public List<CompetitionRound> saveRounds(List<CompetitionRound> rounds) {
        List<CompetitionRound> saved = new ArrayList<>();
        for (CompetitionRound round : rounds) {
            saved.add(saveCompetitionRound(round));
        }
        return saved;
    }

    public CompetitionRound saveCompetitionRound(CompetitionRound round) {
        for (FootballMatch match : round.getMatches()) {
            saveMatch(match);
        }
        var saved = competitionRoundRepository.save(round);
        log.debug("Competition round with ID [{}] saved", saved.getId());
        return saved;
    }

    private void saveMatchesWithCompIds(FootballMatch match, Long compRoundId, Long competitionId) {
        match.setCompetitionId(competitionId);
        match.setCompetitionRoundId(compRoundId);
        saveMatch(match);
    }

    private void saveMatch(FootballMatch match) {
        var matchId = footballMatchDataLayer.createMatch(match).getId();
        log.debug("Match with ID [{}] saved.", matchId);
    }

    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> findAllCompetitions() {
        return competitionRepository.findAll();
    }

    public void delete(long id) {
        competitionRepository.deleteById(id);
    }
}
