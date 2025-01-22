package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.persistence.CompetitionRoundDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionRoundService {
    private final CompetitionRoundDataLayer competitionRoundDataLayer;

    public Either<String, CompetitionRound> findById(long id) {
        return competitionRoundDataLayer.findById(id)
                .map(Either::<String, CompetitionRound>right)
                .orElseGet(() -> {
                    String warnMsg = "Competition round with ID [%s] not found.".formatted(id);
                    log.warn(warnMsg);
                    return Either.left(warnMsg);
                });

    }
}
