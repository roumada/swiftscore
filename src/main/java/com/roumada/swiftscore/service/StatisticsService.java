package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.response.FootballClubStatisticsResponseDTO;
import com.roumada.swiftscore.model.dto.response.StandingsResponseDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.model.mapper.FootballMatchMapper;
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
        for (FootballClub fc : comp.getParticipants())
            standingsForFC.put(fc.getId(), new StandingsResponseDTO(fc.getName()));

        for (CompetitionRound cr : comp.getRounds()) {
            for (FootballMatch fm : cr.getMatches()) {
                processMatch(fm);
            }
        }

        for (FootballClub fc : comp.getParticipants()) {
            standingsForFC.get(fc.getId())
                    .setStatistics(footballMatchDataLayer
                            .findAllMatchesForClubInCompetition(competitionId, fc.getId(), 0, false)
                            .stream()
                            .map(FootballMatchMapper.INSTANCE::matchToMatchResponse)
                            .toList());
        }

        return Either.right(standingsForFC.values()
                .stream()
                .sorted(Comparator
                        .comparingInt(StandingsResponseDTO::getPoints)
                        .thenComparing(StandingsResponseDTO::getWins)
                        .thenComparing(StandingsResponseDTO::getDraws)
                        .thenComparing(StandingsResponseDTO::getGoalsScored)
                        .reversed())
                .toList());
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

    public Either<String, FootballClubStatisticsResponseDTO> getForClub(long clubId, int page,
                                                                        boolean includeUnresolved) {
        var optionalFC = footballClubDataLayer.findById(clubId);
        if (optionalFC.isEmpty()) {
            String errorMsg = "Couldn't find club with ID [%s]".formatted(clubId);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        var fc = optionalFC.get();
        var statsDTO = footballMatchDataLayer
                .findAllMatchesForClub(fc.getId(), page, includeUnresolved)
                .stream()
                .map(FootballMatchMapper.INSTANCE::matchToMatchResponse)
                .toList();
        var fcDTO = FootballClubMapper.INSTANCE.objectToRequest(fc);
        return Either.right(new FootballClubStatisticsResponseDTO(fcDTO, statsDTO));
    }
}
