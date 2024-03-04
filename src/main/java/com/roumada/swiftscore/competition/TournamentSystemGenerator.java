package com.roumada.swiftscore.competition;

import com.roumada.swiftscore.model.MonoPair;

import java.util.ArrayList;
import java.util.List;

public class TournamentSystemGenerator {

    private TournamentSystemGenerator() {
    }

    public static List<List<MonoPair<Integer>>> generateRoundRobinNumericMatchups(int leagueSize) {
        int totalRounds = leagueSize - 1;

        List<List<MonoPair<Integer>>> allMatchWeekIds = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            List<MonoPair<Integer>> matchWeekIds = new ArrayList<>();
            List<MonoPair<Integer>> matchWeekIdsInverted = new ArrayList<>();
            for (int match = 0; match < leagueSize / 2; match++) {
                int home = (round + match) % (leagueSize - 1);
                int away = (leagueSize - 1 - match + round) % (leagueSize - 1);

                if (match == 0) {
                    away = leagueSize - 1;
                }

                matchWeekIds.add(MonoPair.of(home, away));
                matchWeekIdsInverted.add(MonoPair.of(home, away).invert());
            }
            allMatchWeekIds.add(matchWeekIds);
            allMatchWeekIds.add(matchWeekIdsInverted);
        }

        return allMatchWeekIds;
    }
}
