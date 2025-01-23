package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballMatchDataLayer {

    private FootballMatchRepository footballMatchRepository;

    public FootballMatch createMatch(FootballMatch match) {
        setIdsInStatistics(match.getHomeSideStatistics(), match.getId(), match.getCompetitionId());
        setIdsInStatistics(match.getAwaySideStatistics(), match.getId(), match.getCompetitionId());
        return footballMatchRepository.save(match);
    }

    private void setIdsInStatistics(FootballMatchStatistics statistics, Long matchId, Long competitionId) {
        statistics.setFootballMatchId(matchId);
        statistics.setCompetitionId(competitionId);
    }

    public Optional<FootballMatch> findMatchById(long id) {
        return footballMatchRepository.findById(id);
    }

    public List<Object> findMatchStatisticsForClub(FootballClub footballClub, int page, boolean includeUnresolved) {
        // TODO
        return Collections.emptyList();
    }

    public List<Object> findMatchStatisticsForClubInCompetition(Long competitionId, FootballClub footballClub, int page, boolean includeUnresolved) {
        // TODO
        //        PageRequest pageRequest = PageRequest.of(page, 5);
        //
        //        Page<FootballMatchStatistics> pageResult = includeUnresolved ?
        //                footballMatchStatisticsRepository
        //                        .findByFootballClubIdAndCompetitionId(competitionId, footballClub.getId(), pageRequest) :
        //                footballMatchStatisticsRepository.findByFootballClubIdAndCompetitionIdExcludeUnfinished(competitionId, footballClub.getId(), pageRequest);

        return Collections.emptyList();
    }

    public void deleteByCompetitionId(long id) {
        footballMatchRepository.deleteByCompetitionId(id);
    }
}
