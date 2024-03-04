package com.roumada.swiftscore.competition.operator;

import com.roumada.swiftscore.model.match.MatchWeek;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MatchWeekOperator {
    private int currentMatchWeek = 1;
    private final List<MatchWeek> matchWeeks;

    public MatchWeekOperator(List<MatchWeek> matchWeeks) {
        this.matchWeeks = matchWeeks;
    }

    public MatchWeek getCurrent() {
        return matchWeeks.get(currentMatchWeek - 1);
    }

    public MatchWeek getPrevious() {
        return matchWeeks.get(currentMatchWeek - 2);
    }

    public void incrementMatchWeekCounter() {
        currentMatchWeek++;
    }
}
