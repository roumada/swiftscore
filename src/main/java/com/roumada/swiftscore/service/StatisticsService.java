package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.response.FootballClubStatisticsResponseDTO;
import com.roumada.swiftscore.model.dto.response.StandingsResponseDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
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
    private Map<Long, StandingsResponseDTO> standingsForFC;

    private static void addGoals(FootballMatch match, StandingsResponseDTO standingsForHomeSide, StandingsResponseDTO standingsForAwaySide) {
        standingsForHomeSide.addGoalsScored(match.getHomeSideGoalsScored());
        standingsForHomeSide.addGoalsConceded(match.getAwaySideGoalsScored());
        standingsForAwaySide.addGoalsScored(match.getAwaySideGoalsScored());
        standingsForAwaySide.addGoalsConceded(match.getHomeSideGoalsScored());
    }

    public Either<String, List<StandingsResponseDTO>> getForCompetition(Long competitionId) {
        Optional<Competition> optCompetition = competitionDataLayer.findCompetitionById(competitionId);
        if (optCompetition.isEmpty()) {
            String warnMsg = "Couldn't find competition with ID [%s]".formatted(competitionId);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }


        var comp = optCompetition.get();
        standingsForFC = new HashMap<>();
        for (FootballClub fc : comp.getParticipants()) standingsForFC.put(fc.getId(), new StandingsResponseDTO(fc.getName()));

        for (CompetitionRound cr : comp.getRounds()) {
            for (FootballMatch fm : cr.getMatches()) {
                processMatch(fm);
            }
        }
        // TODO
        //        for (FootballClub fc : comp.getParticipants()) {
        //            standingsForFC.get(fc.getId())
        //                    .setStatistics(footballMatchDataLayer.findMatchStatisticsForClubInCompetition(competitionId, fc, 0, false)
        //                            .stream()
        //                            .map(FootballMatchStatisticsMapper.INSTANCE::statisticsToStatisticsDTO)
        //                            .toList());


        return Either.right(Collections.emptyList());
    }

    private void processMatch(FootballMatch match) {
        StandingsResponseDTO standingsForHomeSide = standingsForFC.get(match.getHomeSideFootballClub().getId());
        StandingsResponseDTO standingsForAwaySide = standingsForFC.get(match.getAwaySideFootballClub().getId());

        switch (match.getMatchResult()) {
            case UNFINISHED ->
                    log.debug("Match with ID [{}] is unfinished. Not including it in standings", match.getId());
            case HOME_SIDE_VICTORY -> {
                addGoals(match, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addWin();
                standingsForAwaySide.addLoss();
            }
            case AWAY_SIDE_VICTORY -> {
                addGoals(match, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addLoss();
                standingsForAwaySide.addWin();
            }
            case DRAW -> {
                addGoals(match, standingsForHomeSide, standingsForAwaySide);
                standingsForHomeSide.addDraw();
                standingsForAwaySide.addDraw();
            }
        }
    }

    public Either<String, FootballClubStatisticsResponseDTO> getForClub(long clubId, int page, boolean includeUnresolved) {
        var optionalFC = footballClubDataLayer.findById(clubId);
        if (optionalFC.isEmpty()) {
            String errorMsg = "Couldn't find club with ID [%s]".formatted(clubId);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }
//
//        var fc = optionalFC.get();
//        var statsDTO = footballMatchDataLayer
//                .findMatchStatisticsForClub(fc, page, includeUnresolved)
//                .stream()
//                .map(FootballMatchStatisticsMapper.INSTANCE::statisticsToStatisticsDTO)
//                .toList();
//        var fcDTO = FootballClubMapper.INSTANCE.objectToRequest(fc);
        return Either.right(new FootballClubStatisticsResponseDTO(null, null));
    }
}
