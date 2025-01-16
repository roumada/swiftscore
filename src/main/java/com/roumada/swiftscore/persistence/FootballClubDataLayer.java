package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
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

    public Either<String, FootballClub> saveFromDto(FootballClubDTO dto) {
        FootballClub footballClub;
        try {
            footballClub = FootballClubMapper.INSTANCE.footballClubDTOtoFootballClub(dto);
        } catch (IllegalArgumentException iae) {
            return Either.left(iae.getLocalizedMessage());
        }
        return Either.right(save(footballClub));
    }

    public FootballClub save(FootballClub footballClub) {
        var saved = repository.save(footballClub);
        log.debug("Football club with data [{}] saved.", footballClub);
        return saved;
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
}
