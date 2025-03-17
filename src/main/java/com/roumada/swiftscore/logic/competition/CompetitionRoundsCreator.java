package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CompetitionRoundsCreator {

    private CompetitionRoundsCreator() {
    }

    public static Either<String, List<CompetitionRound>> create(List<FootballClub> clubs) {
        if (clubs.size() % 2 == 1) {
            String errorMsg = "Unable to generate competition rounds for odd amount of clubs.";
            log.error(errorMsg);
            return Either.left(errorMsg);
        }

        List<List<Pair<Integer, Integer>>> numericRoundRobinMatchups = generateRoundRobinNumericMatchups(clubs.size());
        return Either.right(createCompetitionRoundsOnNumericMatchups(clubs, numericRoundRobinMatchups));
    }

    private static List<CompetitionRound> createCompetitionRoundsOnNumericMatchups(List<FootballClub> clubs,
                                                                                   List<List<Pair<Integer, Integer>>> numericRoundRobinMatchups) {
        List<CompetitionRound> competitionRounds = new ArrayList<>();
        int roundCounter = 1;

        for (List<Pair<Integer, Integer>> numericCompRound : numericRoundRobinMatchups) {
            List<FootballMatch> compRoundMatches = new ArrayList<>();

            for (Pair<Integer, Integer> numericMatch : numericCompRound) {
                compRoundMatches.add(new FootballMatch(clubs.get(numericMatch.getFirst()), clubs.get(numericMatch.getSecond())));
            }
            competitionRounds.add(new CompetitionRound(roundCounter++, compRoundMatches));
        }

        return competitionRounds;
    }

    private static List<List<Pair<Integer, Integer>>> generateRoundRobinNumericMatchups(int leagueSize) {
        int totalRounds = leagueSize - 1;

        List<List<Pair<Integer, Integer>>> allCompRoundsIds = new ArrayList<>();

        for (int round = 0; round < totalRounds; round++) {
            List<Pair<Integer, Integer>> compRoundIds = new ArrayList<>();
            List<Pair<Integer, Integer>> invertedCompRoundIds = new ArrayList<>();
            for (int match = 0; match < leagueSize / 2; match++) {
                int home = (round + match) % (leagueSize - 1);
                int away = (leagueSize - 1 - match + round) % (leagueSize - 1);

                if (match == 0) {
                    away = leagueSize - 1;
                }

                compRoundIds.add(Pair.of(home, away));
                invertedCompRoundIds.add(Pair.of(away, home));
            }
            allCompRoundsIds.add(compRoundIds);
            allCompRoundsIds.add(invertedCompRoundIds);
        }

        return allCompRoundsIds;
    }
}
