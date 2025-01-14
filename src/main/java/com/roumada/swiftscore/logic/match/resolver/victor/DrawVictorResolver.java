package com.roumada.swiftscore.logic.match.resolver.victor;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;

public class DrawVictorResolver implements VictorResolver{
    @Override
    public void resolve(FootballMatch footballMatch) {
        footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.DRAW);
        footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.DRAW);
    }
}
