package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballMatchDataLayer {

    private FootballMatchRepository footballMatchRepository;
    private FootballMatchStatisticsRepository footballMatchStatisticsRepository;

    public FootballMatch saveMatch(FootballMatch match) {
        saveStatistics(match.getHomeSideStatistics());
        saveStatistics(match.getAwaySideStatistics());
        return footballMatchRepository.save(match);
    }

    public void saveStatistics(FootballMatchStatistics statistics) {
        var saved = footballMatchStatisticsRepository.save(statistics);
        log.info("Match statistics with data [{}] saved.", saved);
    }

    public Optional<FootballMatch> findMatchById(long id) {
        return footballMatchRepository.findById(id);
    }
}
