package com.roumada.swiftscore.logic.competition.operator;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompetitionRoundOperator {
    private Competition competition;

    public CompetitionRound getCurrent() {
        return competition.getRounds().get(competition.getCurrentRound() - 1);
    }

    public CompetitionRound getPrevious() {
        return competition.getRounds().get(competition.getCurrentRound() - 2);
    }

    public void incrementCurrentCompetitionRoundCounter() {
        competition.setCurrentRound(competition.getCurrentRound() + 1);
    }
}
