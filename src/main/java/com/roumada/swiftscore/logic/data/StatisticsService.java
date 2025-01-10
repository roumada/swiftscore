package com.roumada.swiftscore.logic.data;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.MonoPair;
import com.roumada.swiftscore.model.dto.StandingsDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final CompetitionDataLayer competitionDataLayer;
    private final FootballClubDataLayer footballClubDataLayer;
    private final FootballMatchDataLayer footballMatchDataLayer;
    private Map<Long, StandingsDTO> standingsForFC;

    private static void addGoals(MonoPair<FootballMatchStatistics> stats, StandingsDTO standingsForHomeSide, StandingsDTO standingsForAwaySide) {
        standingsForHomeSide.addGoalsScored(stats.getLeft().getGoalsScored());
        standingsForHomeSide.addGoalsConceded(stats.getRight().getGoalsScored());
        standingsForAwaySide.addGoalsScored(stats.getRight().getGoalsScored());
        standingsForAwaySide.addGoalsConceded(stats.getLeft().getGoalsScored());
    }

    public Either<String, List<StandingsDTO>> getForCompetition(Long competitionId) {
        Optional<Competition> optCompetition = competitionDataLayer.findCompetitionById(competitionId);
        if (optCompetition.isEmpty()) {
            String errorMsg = "Couldn't find competition with ID [%s]".formatted(competitionId);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }


        var comp = optCompetition.get();
        standingsForFC = new HashMap<>();
        for (FootballClub fc : comp.getParticipants()) standingsForFC.put(fc.getId(), new StandingsDTO(fc.getName()));

        for (CompetitionRound cr : comp.getRounds()) {
            for (FootballMatch fm : cr.getMatches()) {
                processMatch(fm);
            }
        }

        return Either.right(standingsForFC.values().stream().sorted(Comparator.comparingInt(StandingsDTO::getPoints).reversed()).toList());
    }

    private void processMatch(FootballMatch match) {
        var stats = match.getStatistics();
        StandingsDTO standingsForHomeSide = standingsForFC.get(stats.getLeft().getFootballClubId());
        StandingsDTO standingsForAwaySide = standingsForFC.get(stats.getRight().getFootballClubId());

        switch (match.getMatchResult()) {
            case UNFINISHED ->
                    log.info("Match with ID [{}] is unfinished. Not including it in standings", match.getId());
            case HOME_SIDE_VICTORY -> {
                addGoals(stats, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addWin();
                standingsForAwaySide.addLoss();
            }
            case AWAY_SIDE_VICTORY -> {
                addGoals(stats, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addLoss();
                standingsForAwaySide.addWin();
            }
            case DRAW -> {
                addGoals(stats, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addDraw();
                standingsForAwaySide.addDraw();
            }
        }
    }

    public Either<String, List<FootballMatchStatistics>> getForClub(long clubId) {
        var optionalFC = footballClubDataLayer.findById(clubId);
        if (optionalFC.isEmpty()) {
            String errorMsg = "Couldn't find club with ID [%s]".formatted(clubId);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        var fc = optionalFC.get();
        return Either.right(footballMatchDataLayer.findMatchStatisticsForClub(fc));
    }
}
