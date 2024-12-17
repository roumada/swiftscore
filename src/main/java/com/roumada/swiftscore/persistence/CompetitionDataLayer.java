package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.data.mapper.CompetitionMapper;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;
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

    public Optional<Competition> generateAndSave(CompetitionRequestDTO dto) {
        var footballClubs = new ArrayList<FootballClub>();
        for (Long id : dto.participantIds()) {
            footballClubs.add(footballClubRepository.findById(id).orElse(null));
        }

        if (footballClubs.contains(null)) {
            log.error("Failed to generate competition - failed to retrieve at least one club from the database.");
            return Optional.empty();
        }
        var rounds = CompetitionRoundsGenerator.generate(footballClubs);
        saveRounds(rounds);

        var competition = CompetitionMapper.INSTANCE.competitionRequestDTOToCompetition(dto);
        competition.setParticipants(footballClubs);
        competition.setRounds(rounds);
        competitionRepository.save(competition);
        return Optional.of(competition);
    }

    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> findAllComps() {
        return competitionRepository.findAll();
    }

    public CompetitionRound saveCompetitionRound(CompetitionRound round) {
        for (FootballMatch match : round.getMatches()) {
            footballMatchDataLayer.saveStatistics(match.getHomeSideStatistics());
            footballMatchDataLayer.saveStatistics(match.getAwaySideStatistics());
            footballMatchDataLayer.saveMatch(match);
        }
        return competitionRoundRepository.save(round);
    }

    private void saveRounds(List<CompetitionRound> rounds) {
        for (CompetitionRound round : rounds) {
            saveCompetitionRound(round);
        }
    }

    public Competition saveCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }
}
