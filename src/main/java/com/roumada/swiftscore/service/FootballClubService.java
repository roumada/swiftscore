package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import io.vavr.control.Either;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballClubService {

    private final FootballClubRepository repository;

    public Either<String, FootballClub> findById(long id) {
        return null;
    }

    public List<FootballClub> findAll() {
        return Collections.emptyList();
    }

    public FootballClub save(FootballClubDTO dto) {
        return null;
    }

    public Either<String, FootballClub> update(Long id, FootballClubDTO dto) {
        return null;
    }
}
