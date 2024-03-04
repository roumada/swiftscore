package com.roumada.swiftscore.competition.manager;

import com.roumada.swiftscore.competition.operator.MatchWeekOperator;
import com.roumada.swiftscore.match.MatchWeekSimulator;
import com.roumada.swiftscore.model.match.MatchWeek;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchWeekManager {
    private MatchWeekOperator matchWeekOperator;
    private MatchWeekSimulator matchWeekSimulator;

    public void simulateMatchWeek() {
        matchWeekSimulator.simulate(getCurrentMatchWeek());
        matchWeekOperator.incrementMatchWeekCounter();
    }

    public MatchWeek getCurrentMatchWeek() {
        return matchWeekOperator.getCurrent();
    }

    public MatchWeek getPreviousMatchWeek() {
        return matchWeekOperator.getPrevious();
    }
}
