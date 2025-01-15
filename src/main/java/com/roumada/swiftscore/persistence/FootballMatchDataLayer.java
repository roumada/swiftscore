package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballMatchDataLayer {

    private FootballMatchRepository footballMatchRepository;
    private FootballMatchStatisticsRepository footballMatchStatisticsRepository;

    public FootballMatch createMatch(FootballMatch match) {
        saveMatchWithStatistics(match);
        setIdsInStatistics(match.getHomeSideStatistics(), match.getId(), match.getCompetitionId());
        setIdsInStatistics(match.getAwaySideStatistics(), match.getId(), match.getCompetitionId());
        return saveMatchWithStatistics(match);
    }

    public FootballMatch saveMatchWithStatistics(FootballMatch match) {
        saveStatistics(match.getHomeSideStatistics());
        saveStatistics(match.getAwaySideStatistics());
        return footballMatchRepository.save(match);
    }

    public void saveStatistics(FootballMatchStatistics statistics) {
        var saved = footballMatchStatisticsRepository.save(statistics);
        log.debug("Match statistics with data [{}] saved.", saved);
    }

    private void setIdsInStatistics(FootballMatchStatistics statistics, Long matchId, Long competitionId) {
        statistics.setFootballMatchId(matchId);
        statistics.setCompetitionId(competitionId);
    }

    public Optional<FootballMatch> findMatchById(long id) {
        return footballMatchRepository.findById(id);
    }

    public List<FootballMatchStatistics> findMatchStatisticsForClub(FootballClub footballClub, int page, boolean includeUnresolved) {
        PageRequest pageRequest = PageRequest.of(page, 5);

        Page<FootballMatchStatistics> pageResult = includeUnresolved ?
                footballMatchStatisticsRepository.findByFootballClubId(footballClub.getId(), pageRequest) :
                footballMatchStatisticsRepository.findByFootballClubIdExcludeUnfinished(footballClub.getId(), pageRequest);

        return pageResult.getContent();
    }

    public List<FootballMatchStatistics> findMatchStatisticsForClubInCompetition(Long competitionId, FootballClub footballClub, int page, boolean includeUnresolved) {
        PageRequest pageRequest = PageRequest.of(page, 5);

        Page<FootballMatchStatistics> pageResult = includeUnresolved ?
                footballMatchStatisticsRepository
                        .findByFootballClubIdAndCompetitionId(competitionId, footballClub.getId(), pageRequest) :
                footballMatchStatisticsRepository.findByFootballClubIdAndCompetitionIdExcludeUnfinished(competitionId, footballClub.getId(), pageRequest);

        return pageResult.getContent();
    }
}
