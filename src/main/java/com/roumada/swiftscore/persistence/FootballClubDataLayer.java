package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballClubDataLayer {

    private final FootballClubRepository repository;

    public FootballClub save(FootballClub footballClub) {
        return repository.save(footballClub);
    }

    public Optional<FootballClub> findById(long id) {
        return repository.findById(id);
    }
}
