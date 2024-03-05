package com.roumada.swiftscore.competition.manager;

import com.roumada.swiftscore.competition.operator.CompetitionRoundOperator;
import com.roumada.swiftscore.match.CompetitionRoundSimulator;
import com.roumada.swiftscore.model.match.CompetitionRound;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompetitionRoundManager {
    private CompetitionRoundOperator competitionRoundOperator;
    private CompetitionRoundSimulator competitionRoundSimulator;

    public void simulateRound() {
        competitionRoundSimulator.simulate(getCurrentCompetitionRound());
        competitionRoundOperator.incrementCurrentCompetitionRoundCounter();
    }

    public CompetitionRound getCurrentCompetitionRound() {
        return competitionRoundOperator.getCurrent();
    }

    public CompetitionRound getPreviousCompetitionRound() {
        return competitionRoundOperator.getPrevious();
    }
}
