package com.roumada.swiftscore.util;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import com.roumada.swiftscore.model.dto.request.CreateLeagueCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;

import java.util.List;

public class LeagueTestUtils {
    public static CreateLeagueRequest getCreateLeagueRequest(int participants1, int participants2) {
        return new CreateLeagueRequest(
                "League",
                CountryCode.GB,
                "2020-08-01",
                "2021-06-01",
                getCreateLeagueCompetitionRequests(participants1, participants2)
        );
    }

    public static List<CreateLeagueCompetitionRequest> getCreateLeagueCompetitionRequests(int participants1,
                                                                                          int participants2) {
        return List.of(new CreateLeagueCompetitionRequest(
                "Competition 1",
                new CompetitionParameters(participants1, null, 0),
                new SimulationParameters(0)),
                new CreateLeagueCompetitionRequest(
                "Competition 2",
                new CompetitionParameters(participants2, null, 0),
                new SimulationParameters(0)
        ));
    }
}
