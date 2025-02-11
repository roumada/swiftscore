package com.roumada.swiftscore.logic.match.resolver.extra;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class HomeSideExtraScoreResolver implements ExtraScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int bonusGoalsCeiling = Math.min(6,
                (int) (footballMatch.getHomeSideCalculatedVictoryChance() - footballMatch.getAwaySideCalculatedVictoryChance()) * 10);
        int extraGoals = HorusSeries.getFromOne(bonusGoalsCeiling, ThreadLocalRandom.current().nextDouble());
        footballMatch.setHomeSideGoalsScored(footballMatch.getHomeSideGoalsScored() + extraGoals);
        footballMatch.setExtraVictorGoals(extraGoals);
    }
}
