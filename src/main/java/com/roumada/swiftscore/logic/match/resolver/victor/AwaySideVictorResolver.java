package com.roumada.swiftscore.logic.match.resolver.victor;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;

public class AwaySideVictorResolver implements VictorResolver{
    @Override
    public void resolve(FootballMatch footballMatch) {
        footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.LOSS);
        footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.VICTORY);
    }
}
