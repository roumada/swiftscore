package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompetitionRoundDataLayer {
    private final CompetitionRoundRepository competitionRoundRepository;

    public CompetitionRound save(CompetitionRound round) {
        return competitionRoundRepository.save(round);
    }

    public void deleteByCompetitionId(long id) {
        competitionRoundRepository.deleteByCompetitionId(id);
    }

    public Optional<CompetitionRound> findById(long id) {
        return competitionRoundRepository.findById(id);
    }
}
