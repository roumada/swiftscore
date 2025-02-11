package com.roumada.swiftscore.logic.match.resolver.extra;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class AwaySideExtraScoreResolver implements ExtraScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int bonusGoalsCeiling = Math.min(6,
                (int) (footballMatch.getAwaySideCalculatedVictoryChance() - footballMatch.getHomeSideCalculatedVictoryChance()) * 10);
        int extraGoals = HorusSeries.getFromOne(bonusGoalsCeiling, ThreadLocalRandom.current().nextDouble());
        footballMatch.setAwaySideGoalsScored(footballMatch.getAwaySideGoalsScored() + extraGoals);
        footballMatch.setExtraVictorGoals(extraGoals);
    }
}
