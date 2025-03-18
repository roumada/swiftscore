package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateLeagueCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.model.dto.response.LeagueSimulationResponse;
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.model.organization.league.LeagueSeason;
import com.roumada.swiftscore.persistence.datalayer.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.datalayer.LeagueDataLayer;
import com.roumada.swiftscore.util.Messages;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest.fromMergedRequests;

@Service
@AllArgsConstructor
public class LeagueService {

    private final CompetitionService competitionService;
    private final CompetitionDataLayer competitionDataLayer;
    private final LeagueDataLayer leagueDataLayer;

    public Either<ErrorResponse, League> createFromRequest(CreateLeagueRequest leagueRequest) {
        if(participantIdsAreNotUnique(leagueRequest))
            return Either.left(new ErrorResponse(List.of(Messages.LEAGUE_DUPLICATED_PARTICIPANT_IDS.format())));

        var errors = new ArrayList<String>();
        var createdCompetitionIds = new ArrayList<Long>();
        var participatingClubIds = new ArrayList<Long>();


        for (CreateLeagueCompetitionRequest competitionRequest : leagueRequest.competitions()) {
            participatingClubIds.addAll(competitionRequest.participantIds());
        }

        for (CreateLeagueCompetitionRequest competitionRequest : leagueRequest.competitions()) {
            var generationResult = competitionService.generateAndSave(
                    fromMergedRequests(leagueRequest, competitionRequest),
                    participatingClubIds);

            generationResult.fold(
                    errors::add,
                    competition -> {
                        createdCompetitionIds.add(competition.getId());
                        participatingClubIds.addAll(competition.getParticipants().stream().map(FootballClub::getId).toList());
                        return competition;
                    }
            );
        }

        if (!errors.isEmpty()) {
            return Either.left(new ErrorResponse(errors));
        }

        var leagueSeason = new LeagueSeason(leagueRequest.determineSeason(), createdCompetitionIds);
        var league = new League(leagueRequest.name(), List.of(leagueSeason));
        return Either.right(leagueDataLayer.save(league));
    }

    public Either<ErrorResponse, League> findById(long id) {
        var result = leagueDataLayer.findById(id);
        return result.isPresent() ?
                Either.right(result.get()) :
                Either.left(new ErrorResponse(List.of(Messages.LEAGUE_NOT_FOUND.format(id))));
    }

    public void deleteById(long id) {
        leagueDataLayer.deleteById(id);
    }

    private boolean participantIdsAreNotUnique(CreateLeagueRequest leagueRequest) {
        var total = leagueRequest.competitions().stream()
                .mapToInt(a -> a.participantIds().size())
                .sum();
        var distinct = leagueRequest.competitions().stream()
                .flatMap(a -> a.participantIds().stream())
                .distinct()
                .count();
        return total != distinct;
    }

    public Either<ErrorResponse, LeagueSimulationResponse> simulate(long id, int times) {
        var result = leagueDataLayer.findById(id);
        if(result.isEmpty()) return Either.left(new ErrorResponse(List.of(Messages.LEAGUE_NOT_FOUND.format(id))));
        var league = result.get();
        var compIds = league.latestSeason().competitionIds();
        List<Integer> timesSimulated = new ArrayList<>();

        for(Long competitionId : compIds){
            competitionService.simulate(competitionDataLayer.findCompetitionById(competitionId).get(), times).fold(
                    error -> timesSimulated.add(0),
                    rounds -> timesSimulated.add(rounds.size())
            );
        }

        return Either.right(new LeagueSimulationResponse(league.getId(), compIds, timesSimulated));
    }
}
