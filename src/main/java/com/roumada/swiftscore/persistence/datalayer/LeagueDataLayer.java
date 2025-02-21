package com.roumada.swiftscore.persistence.datalayer;

import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.persistence.repository.LeagueRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class LeagueDataLayer {

    private final LeagueRepository repository;

    public League save(League league){
        return repository.save(league);
    }

    public Optional<League> findbyId(Long id){
        return repository.findById(id);
    }
}
