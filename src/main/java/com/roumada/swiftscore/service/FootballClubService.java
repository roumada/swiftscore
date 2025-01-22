package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.FootballClubRequestDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FootballClubService {

    private final FootballClubRepository repository;

    public Either<String, FootballClub> findById(long id) {
        var findResult = repository.findById(id);
        if (findResult.isEmpty()) {
            String errorMsg = "Unable to find football club with given id [%s]".formatted(id);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        } else return Either.right(findResult.get());
    }

    public List<FootballClub> findAll() {
        return repository.findAll();
    }

    public FootballClub save(FootballClubRequestDTO dto) {
        return repository.save(FootballClubMapper.INSTANCE.requestToObject(dto));
    }

    public Either<String, FootballClub> update(Long id, FootballClubRequestDTO dto) {
        var findResult = repository.findById(id);
        if (findResult.isEmpty()) {
            String errorMsg = "Unable to find football club with given id [%s]".formatted(id);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        if (dto.victoryChance() != 0.0 && (dto.victoryChance() < 0 || dto.victoryChance() > 1)) {
            String errorMsg = "Victory chance cannot be lower than 0 or higher than 1";
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        var footballClub = findResult.get();
        updateFields(dto, footballClub);

        return Either.right(repository.save(footballClub));
    }

    private void updateFields(FootballClubRequestDTO dto, FootballClub footballClub) {
        if (dto.name() != null) {
            footballClub.setName(dto.name());
        }
        if (dto.country() != null) {
            footballClub.setCountry(dto.country());
        }
        if (dto.stadiumName() != null) {
            footballClub.setStadiumName(dto.stadiumName());
        }
        if (dto.victoryChance() != 0.0) {
            footballClub.setVictoryChance(dto.victoryChance());
        }
    }
}
