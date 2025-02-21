package com.roumada.swiftscore.util;

import com.roumada.swiftscore.logic.competition.CompetitionCreator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.dto.CompetitionParameters;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.organization.Competition;

import java.util.List;

import static com.neovisionaries.i18n.CountryCode.GB;

public class CompetitionTestUtils {

    private CompetitionTestUtils() {
    }

    public static CreateCompetitionRequest getRequest(List<FootballClub> clubs) {
        return createRequest(0, FootballClubTestUtils.getIdsOfSavedClubs(clubs));
    }

    public static Object getRequest(int participants) {
        return createRequest(participants, null);
    }

    public static CreateCompetitionRequest getRequest(int participants, List<FootballClub> clubs) {
        return createRequest(participants, FootballClubTestUtils.getIdsOfSavedClubs(clubs));
    }

    public static CreateCompetitionRequest getRequest(SimulationParameters simulationParameters, List<FootballClub> clubs) {
        return new CreateCompetitionRequest("Competition",
                GB,
                "2024-07-01",
                "2025-04-30",
                new CompetitionParameters(0, FootballClubTestUtils.getIdsOfSavedClubs(clubs), 0),
                simulationParameters);
    }

    private static CreateCompetitionRequest createRequest(int participants, List<Long> clubs) {
        return new CreateCompetitionRequest("Competition",
                GB,
                "2024-07-01",
                "2025-04-30",
                new CompetitionParameters(participants, clubs, 0),
                new SimulationParameters(0.1, 0.2, 0.3));
    }

    public static Competition get(List<FootballClub> clubs) {
        var request = getRequest(clubs);

        return CompetitionCreator.createFromRequest(request, clubs).get();
    }
}
