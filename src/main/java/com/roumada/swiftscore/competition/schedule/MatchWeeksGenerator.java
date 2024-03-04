package com.roumada.swiftscore.competition.schedule;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.MonoPair;
import com.roumada.swiftscore.model.match.FootballClubMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.MatchWeek;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
public class MatchWeeksGenerator {

    private MatchWeeksGenerator() {
    }

    public static List<MatchWeek> generateForRoundRobinLeague(List<FootballClub> clubs) {
        if (clubs.size() % 2 == 1) {
            log.warn("Unable to generate match weeks for odd amount of clubs. Aborting...");
            return Collections.emptyList();
        }

        List<List<MonoPair<Integer>>> numericRoundRobinMatchups = generateRoundRobinNumericMatchups(clubs.size());

        int weekCounter = 1;
        List<MatchWeek> matchWeeks = new ArrayList<>();

        for (List<MonoPair<Integer>> numericMatchWeek : numericRoundRobinMatchups) {
            List<FootballMatch> matchesForMatchWeek = new ArrayList<>();
            for (MonoPair<Integer> numericMatch : numericMatchWeek) {
                matchesForMatchWeek.add(new FootballMatch(
                        new FootballClubMatchStatistics(clubs.get(numericMatch.getA())),
                        new FootballClubMatchStatistics(clubs.get(numericMatch.getB()))));
            }
            matchWeeks.add(new MatchWeek(weekCounter++, matchesForMatchWeek));
        }
        return matchWeeks;
    }

    private static List<List<MonoPair<Integer>>> generateRoundRobinNumericMatchups(int leagueSize) {
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
