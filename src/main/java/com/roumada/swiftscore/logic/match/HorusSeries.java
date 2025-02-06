package com.roumada.swiftscore.logic.match;

import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;

public class HorusSeries {
    private static final List<Integer> VICTOR_GOALS_DISTRIBUTION = List.of(1, 2, 0, 3, 4, 5, 6, 7, 8, 9);
    private static final List<Integer> NORMAL_GOALS_DISTRIBUTION = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    private HorusSeries() {
    }

    public static int getVictorGoalsScored(int ceil, double chance) {
        List<Double> summedChances = generateCumulativeChance(ceil);
        return getGoalsAccordingToChances(summedChances, chance, true);
    }

    public static int getGoalsScored(int ceil, double chance) {
        List<Double> summedChances = generateCumulativeChance(ceil);
        return getGoalsAccordingToChances(summedChances, chance, false);
    }

    private static List<Double> generateCumulativeChance(int ceil) {
        List<Double> chances = new ArrayList<>();
        chances.add(0.);
        double summand = 0.5;
        double nextValue = 0.5;

        if (ceil == 0) return List.of(0., 1.);
        if (ceil == 1) return List.of(0., summand, 1.);

        chances.add(summand);
        for (int i = 0; i < ceil - 1; i++) {
            nextValue /= 2;
            summand += nextValue;
            chances.add(summand);
        }

        chances.add(1.);

        return chances;
    }

    private static int getGoalsAccordingToChances(List<Double> summedChances, double chance, boolean victor) {
        for (int i = 0; i < summedChances.size() - 1; i++) {
            Range<Double> range = Range.of(summedChances.get(i), summedChances.get(i + 1));
            if (range.contains(chance))
                return victor ? VICTOR_GOALS_DISTRIBUTION.get(i) : NORMAL_GOALS_DISTRIBUTION.get(i);
        }
        return 0;
    }
}
