package com.roumada.swiftscore.logic.match.resolver.victor;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;

public class HomeSideVictorResolver implements VictorResolver{
    @Override
    public void resolve(FootballMatch footballMatch) {
        footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.VICTORY);
        footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.LOSS);
    }
}
