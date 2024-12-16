package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.logic.competition.manager.CompetitionManager;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;

    public CompetitionRound simulateRound(Competition competition) {
        var compSimulated = CompetitionManager.simulateCurrentRound(competition);
        if (compSimulated == null) return null;

        var currRound = competition.getCurrentRound();
        competitionDataLayer.saveRound(currRound);
        competitionDataLayer.saveCompetition(compSimulated);
        return currRound;
    }
}
