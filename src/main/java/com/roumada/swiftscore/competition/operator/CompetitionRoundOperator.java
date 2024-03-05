package com.roumada.swiftscore.competition.operator;

import com.roumada.swiftscore.model.match.CompetitionRound;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CompetitionRoundOperator {
    private int currentCompetitionRound = 1;
    private final List<CompetitionRound> competitionRounds;

    public CompetitionRoundOperator(List<CompetitionRound> competitionRounds) {
        this.competitionRounds = competitionRounds;
    }

    public CompetitionRound getCurrent() {
        return competitionRounds.get(currentCompetitionRound - 1);
    }

    public CompetitionRound getPrevious() {
        return competitionRounds.get(currentCompetitionRound - 2);
    }

    public void incrementCurrentCompetitionRoundCounter() {
        currentCompetitionRound++;
    }
}
