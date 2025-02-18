package com.roumada.swiftscore.logic.match;

import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;

public class HorusSeries {
    private HorusSeries() {
    }

    public static int getFromOne(int ceil, double chance) {
        List<Double> summedChances = generateCumulativeChance(ceil);
        return getGoalsAccordingToChances(summedChances, chance, false);
    }

    public static int getFromZero(int ceil, double chance) {
        List<Double> summedChances = generateCumulativeChance(ceil);
        return getGoalsAccordingToChances(summedChances, chance, true);
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

    private static int getGoalsAccordingToChances(List<Double> summedChances, double chance, boolean isDraw) {
        if(summedChances.size() == 1) return 1;

        for (int i = 0; i < summedChances.size() - 1; i++) {
            Range<Double> range = Range.of(summedChances.get(i), summedChances.get(i + 1));
            if (range.contains(chance)) return isDraw ? i : i + 1;
        }
        return 0;
    }
}
