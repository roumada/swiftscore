package com.roumada.swiftscore.match;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.MonoPair;
import com.roumada.swiftscore.model.match.FootballClubMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.MatchWeek;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.roumada.swiftscore.competition.TournamentSystemGenerator.generateRoundRobinNumericMatchups;

@Slf4j
@RequiredArgsConstructor
public class FootballClubCompetitionMatcher {

    public List<MatchWeek> generateScheduleForRoundRobinLeague(List<FootballClub> clubs) {
        if (clubs.size() % 2 == 1) {
            log.warn("Unable to generate match weeks for odd amount of clubs. Aborting...");
            return Collections.emptyList();
        }

        List<List<MonoPair<Integer>>> roundRobinMatchups =
                generateRoundRobinNumericMatchups(clubs.size());

        int weekCounter = 1;
        List<MatchWeek> matchWeeks = new ArrayList<>();

        for (List<MonoPair<Integer>> numericMatchWeek : roundRobinMatchups) {
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
}
