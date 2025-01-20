package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.persistence.repository.FootballMatchStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FootballMatchStatisticsDataLayer {

    private final FootballMatchStatisticsRepository repository;

    public void deleteAllByCompetitionId(long id){
        repository.deleteByCompetitionId(id);
    }
}
