package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.data.model.match.FootballMatchStatistics;
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

    public FootballMatchStatistics saveStatistics(FootballMatchStatistics statistics){
        return footballMatchStatisticsRepository.save(statistics);
    }

    public Optional<FootballMatch> findMatchById(long id) {
        return footballMatchRepository.findById(id);
    }
}
