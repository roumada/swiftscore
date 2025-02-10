package com.roumada.swiftscore.logic.match.resolver.extra;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class HomeSideExtraScoreResolver implements ExtraScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int bonusGoalsCeiling = Math.min(6,
                (int) (footballMatch.getHomeSideCalculatedVictoryChance() - footballMatch.getAwaySideCalculatedVictoryChance()) * 10);
        int addedGoals = HorusSeries.getFromOne(bonusGoalsCeiling, ThreadLocalRandom.current().nextDouble());
        int extraGoals = footballMatch.getHomeSideGoalsScored() + addedGoals;
        footballMatch.setHomeSideGoalsScored(extraGoals);
        footballMatch.setExtraVictorGoals(addedGoals);
    }
}
