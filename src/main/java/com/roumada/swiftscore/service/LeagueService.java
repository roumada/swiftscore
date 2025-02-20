package com.roumada.swiftscore.service;

import com.roumada.swiftscore.model.ErrorResponse;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
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

    public Either<ErrorResponse, League> createFromRequest(CreateLeagueRequest request) {
        var errors = new ArrayList<String>();
        var createdCompetitionIds = new ArrayList<Long>();

        for (CreateCompetitionRequest competitionRequest : request.competitions()) {
            var generationResult = competitionService.generateAndSave(competitionRequest);
            generationResult.fold(
                    errors::add,
                    competition -> createdCompetitionIds.add(competition.getId())
            );
        }

        if (!errors.isEmpty()) {
            return Either.left(new ErrorResponse(errors));
        }

        var leagueSeason = new LeagueSeason("", createdCompetitionIds);
        return Either.right(new League(request.name(), List.of(leagueSeason)));
    }
}
