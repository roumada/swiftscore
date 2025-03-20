package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.dto.response.CompetitionStandingsResponse;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.league.LeagueSeason;
import com.roumada.swiftscore.persistence.datalayer.CompetitionDataLayer;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeagueSeasonService {

    private final CompetitionDataLayer competitionDataLayer;
    private final StatisticsService statisticsService;
    private final FootballClubService footballClubService;
    private final CompetitionService competitionService;

    public Either<ErrorResponse, LeagueSeason> generateNext(LeagueSeason leagueSeason) {
        List<Competition> competitions = competitionDataLayer.findAllById(leagueSeason.competitionIds());
        List<Long> newCompetitionIds = new ArrayList<>();
        List<CompetitionStandingsResponse> standings = new ArrayList<>();
        for (Competition c : competitions) {
            standings.add(statisticsService.getForCompetition(c.getId(), true).get());
        }

        var comp = competitions.get(0);
        var participants = footballClubService.findAllById(getClubIdsForFirstCompetitionForNextSeason(standings.subList(0, 2)));
        comp.setParticipants(participants);
        var result = competitionService.generateFromConcluded(comp);
        if(result.isLeft()){
            return Either.left(result.getLeft());
        } else {
            newCompetitionIds.add(result.get());
        }

        if (competitions.size() > 2) {
            for (int i = 1; i < competitions.size() - 1; i++) {
                comp = competitions.get(i);
                participants = footballClubService.findAllById(
                        getClubIdsForMiddleCompetitionsForNextSeason(standings.subList(i - 1, i + 2)));
                comp.setParticipants(participants);
                result = competitionService.generateFromConcluded(comp);
                if(result.isLeft()){
                    return Either.left(result.getLeft());
                } else {
                    newCompetitionIds.add(result.get());
                }
            }
        }

        comp = competitions.get(competitions.size() - 1);
        participants = footballClubService.findAllById(
                getClubIdsForLastCompetitionForNextSeason(standings.subList(competitions.size() - 2, competitions.size())));
        comp.setParticipants(participants);
        result = competitionService.generateFromConcluded(comp);
        if(result.isLeft()){
            return Either.left(result.getLeft());
        } else {
            newCompetitionIds.add(result.get());
        }

        return Either.right(new LeagueSeason(leagueSeason.nextSeason(), newCompetitionIds));
    }

    private List<Long> getClubIdsForFirstCompetitionForNextSeason(List<CompetitionStandingsResponse> standings) {
        var relegatedSpots = standings.get(0).relegated().size();
        List<Long> newIds = new ArrayList<>();
        newIds.addAll(standings.get(0).retained());
        newIds.addAll(standings.get(1).retained().subList(0, relegatedSpots));

        return newIds;
    }

    private List<Long> getClubIdsForMiddleCompetitionsForNextSeason(List<CompetitionStandingsResponse> standings) {
        var relegatedInto = standings.get(0).relegated();
        var relegatedSpots = standings.get(1).relegated().size();
        List<Long> newIds = new ArrayList<>();
        newIds.addAll(relegatedInto);
        newIds.addAll(standings.get(1).retained().subList(relegatedInto.size(), standings.get(1).retained().size()));
        newIds.addAll(standings.get(2).retained().subList(0, relegatedSpots));

        return newIds;
    }

    private List<Long> getClubIdsForLastCompetitionForNextSeason(List<CompetitionStandingsResponse> standings) {
        var relegatedInto = standings.get(0).relegated();
        List<Long> newIds = new ArrayList<>();
        newIds.addAll(relegatedInto);
        newIds.addAll(standings.get(1).retained().subList(relegatedInto.size(), standings.get(1).retained().size()));

        return newIds;
    }
}
