package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.model.organization.league.LeagueSeason;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LeagueService {

    private final CompetitionService competitionService;

    public Either<ErrorResponse, League> createFromRequest(CreateLeagueRequest leagueRequest) {
        var errors = new ArrayList<String>();
        var createdCompetitionIds = new ArrayList<Long>();

        for (CreateLeagueCompetitionRequest competitionRequest : leagueRequest.competitions()) {
            var generationResult = competitionService.generateAndSave(map(leagueRequest, competitionRequest));
            generationResult.fold(
                    errors::add,
                    competition -> createdCompetitionIds.add(competition.getId())
            );
        }

        if (!errors.isEmpty()) {
            return Either.left(new ErrorResponse(errors));
        }

        var leagueSeason = new LeagueSeason("", createdCompetitionIds);
        return Either.right(new League(leagueRequest.name(), List.of(leagueSeason)));
    }

    private CreateCompetitionRequest map(CreateLeagueRequest leagueRequest,
                                         CreateLeagueCompetitionRequest competitionRequest){
        return new CreateCompetitionRequest(
                competitionRequest.name(),
                leagueRequest.countryCode(),
                leagueRequest.startDate(),
                leagueRequest.endDate(),
                competitionRequest.competitionParameters(),
                competitionRequest.simulationParameters()
        );
    }
}
