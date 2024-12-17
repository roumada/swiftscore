package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballClubDataLayer {

    private final FootballClubRepository repository;

    public FootballClub save(FootballClub footballClub) {
        return repository.save(footballClub);
    }

    public List<FootballClub> saveAll(List<FootballClub> fcs) {
        return repository.saveAll(fcs);
    }

    public Optional<FootballClub> findById(long id) {
        return repository.findById(id);
    }

    public List<FootballClub> findAll() {
        return repository.findAll();
    }

    public Either<String, FootballClub> saveFromDto(FootballClubDTO dto) {
        FootballClub footballClub;
        try {
            footballClub =
                    FootballClub.builder().name(dto.name()).victoryChance(dto.victoryChance()).build();
        } catch (IllegalArgumentException iae) {
            return Either.left(iae.getLocalizedMessage());
        }
        return Either.right(repository.save(footballClub));
    }
}
