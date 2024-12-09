package com.roumada.swiftscore.competition.schedule;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.MonoPair;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CompetitionRoundsGenerator {

    private CompetitionRoundsGenerator() {
    }

    public static Competition generateForLeague(List<FootballClub> clubs) {
        if (clubs.size() % 2 == 1) {
            log.warn("Unable to generate competition rounds for odd amount of clubs. Aborting...");
            return new Competition();
        }

        List<List<MonoPair<Integer>>> numericRoundRobinMatchups = generateRoundRobinNumericMatchups(clubs.size());

        var comp = new Competition();
        comp.setRounds(createCompetitionRoundsOnNumericMatchups(clubs, numericRoundRobinMatchups));
        return comp;
    }

    private static List<CompetitionRound> createCompetitionRoundsOnNumericMatchups(List<FootballClub> clubs,
                                                                 List<List<MonoPair<Integer>>> numericRoundRobinMatchups) {
        List<CompetitionRound> competitionRounds = new ArrayList<>();
        int roundCounter = 1;

        for (List<MonoPair<Integer>> numericCompRound : numericRoundRobinMatchups) {
            List<FootballMatch> compRoundMatches = new ArrayList<>();

            for (MonoPair<Integer> numericMatch : numericCompRound) {
                compRoundMatches.add(new FootballMatch(
                        new FootballMatchStatistics(clubs.get(numericMatch.getA())),
                        new FootballMatchStatistics(clubs.get(numericMatch.getB()))));
            }

            competitionRounds.add(new CompetitionRound(0, roundCounter++, compRoundMatches));
        }

        return competitionRounds;
    }

    private static List<List<MonoPair<Integer>>> generateRoundRobinNumericMatchups(int leagueSize) {
        int totalRounds = leagueSize - 1;

        List<List<MonoPair<Integer>>> allCompRoundsIds = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            List<MonoPair<Integer>> compRoundIds = new ArrayList<>();
            List<MonoPair<Integer>> invertedCompRoundIds = new ArrayList<>();
            for (int match = 0; match < leagueSize / 2; match++) {
                int home = (round + match) % (leagueSize - 1);
                int away = (leagueSize - 1 - match + round) % (leagueSize - 1);

                if (match == 0) {
                    away = leagueSize - 1;
                }

                compRoundIds.add(MonoPair.of(home, away));
                invertedCompRoundIds.add(MonoPair.of(away, home));
            }
            allCompRoundsIds.add(compRoundIds);
            allCompRoundsIds.add(invertedCompRoundIds);
        }

        return allCompRoundsIds;
    }
}
