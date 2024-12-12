package com.roumada.swiftscore.logic.competition.manager;

import com.roumada.swiftscore.logic.competition.operator.CompetitionRoundOperator;
import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import lombok.Builder;

@Builder
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
