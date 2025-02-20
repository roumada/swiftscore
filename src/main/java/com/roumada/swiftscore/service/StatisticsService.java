package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.response.CompetitionStandingsResponse;
import com.roumada.swiftscore.model.dto.response.FootballClubStatisticsResponse;
import com.roumada.swiftscore.model.dto.response.FootballClubStandings;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.model.mapper.FootballMatchMapper;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.CompetitionRound;
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
    private Map<Long, FootballClubStandings> standingsForFC;

    private static void addGoals(FootballMatch match, FootballClubStandings standingsForHomeSide, FootballClubStandings standingsForAwaySide) {
        standingsForHomeSide.addGoalsScored(match.getHomeSideGoalsScored());
        standingsForHomeSide.addGoalsConceded(match.getAwaySideGoalsScored());
        standingsForAwaySide.addGoalsScored(match.getAwaySideGoalsScored());
        standingsForAwaySide.addGoalsConceded(match.getHomeSideGoalsScored());
    }

    public Either<String, CompetitionStandingsResponse> getForCompetition(Long competitionId, Boolean simplify) {
        Optional<Competition> optCompetition = competitionDataLayer.findCompetitionById(competitionId);
        if (optCompetition.isEmpty()) {
            String warnMsg = "Couldn't find competition with ID [%s]".formatted(competitionId);
            log.warn(warnMsg);
            return Either.left(warnMsg);
        }

        var comp = optCompetition.get();
        standingsForFC = new LinkedHashMap<>();
        for (FootballClub fc : comp.getParticipants())
            standingsForFC.put(fc.getId(), new FootballClubStandings(fc.getName()));

        for (CompetitionRound cr : comp.getRounds()) {
            for (FootballMatch fm : cr.getMatches()) {
                processMatch(fm);
            }
        }

        if (Boolean.FALSE.equals(simplify)) {
            for (FootballClub fc : comp.getParticipants()) {
                standingsForFC.get(fc.getId())
                        .setStatistics(footballMatchDataLayer
                                .findAllMatchesForClubInCompetition(competitionId, fc.getId(), 0, false)
                                .stream()
                                .map(FootballMatchMapper.INSTANCE::matchToMatchResponse)
                                .toList());
            }
        }

        standingsForFC.values().forEach(FootballClubStandings::calculateGoalDifference);

        Map<Long, FootballClubStandings> standings = standingsForFC.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator
                        .comparingInt(FootballClubStandings::getPoints)
                        .thenComparing(FootballClubStandings::getWins)
                        .thenComparing(FootballClubStandings::getDraws)
                        .thenComparing(FootballClubStandings::getGoalDifference)
                        .reversed()))
                .collect(LinkedHashMap::new, (map, e) ->
                        map.put(e.getKey(), e.getValue()), Map::putAll);
        List<Long> sortedIds = new ArrayList<>(standings.keySet());

        return Either.right(new CompetitionStandingsResponse(new ArrayList<>(standings.values()),
                sortedIds.subList(0, sortedIds.size() - comp.getRelegationSpots()),
                sortedIds.subList(sortedIds.size() - comp.getRelegationSpots(), sortedIds.size())
        ));
    }

    private void processMatch(FootballMatch match) {
        FootballClubStandings standingsForHomeSide = standingsForFC.get(match.getHomeSideFootballClub().getId());
        FootballClubStandings standingsForAwaySide = standingsForFC.get(match.getAwaySideFootballClub().getId());

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

    public Either<String, FootballClubStatisticsResponse> getForClub(long clubId, int page, int size,
                                                                     boolean includeUnresolved) {
        var optionalFC = footballClubDataLayer.findById(clubId);
        if (optionalFC.isEmpty()) {
            String errorMsg = "Couldn't find club with ID [%s]".formatted(clubId);
            log.warn(errorMsg);
            return Either.left(errorMsg);
        }

        var fc = optionalFC.get();
        var statsDTO = footballMatchDataLayer
                .findAllMatchesForClub(fc.getId(), page, size, includeUnresolved)
                .stream()
                .map(FootballMatchMapper.INSTANCE::matchToMatchResponse)
                .toList();
        var fcDTO = FootballClubMapper.INSTANCE.objectToRequest(fc);
        return Either.right(new FootballClubStatisticsResponse(fcDTO, statsDTO));
    }
}
