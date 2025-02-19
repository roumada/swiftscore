package com.roumada.swiftscore.util;

import com.roumada.swiftscore.logic.creator.CompetitionCreator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionParametersDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.match.Competition;

import java.util.List;

import static com.neovisionaries.i18n.CountryCode.GB;

public class CompetitionTestUtils {

    private CompetitionTestUtils() {
    }

    public static CreateCompetitionRequestDTO getRequest(List<FootballClub> clubs) {
        return createRequest(0, FootballClubTestUtils.getIdsOfSavedClubs(clubs));
    }

    public static Object getRequest(int participants) {
        return createRequest(participants, null);
    }

    public static CreateCompetitionRequestDTO getRequest(int participants, List<FootballClub> clubs) {
        return createRequest(participants, FootballClubTestUtils.getIdsOfSavedClubs(clubs));
    }

    public static CreateCompetitionRequestDTO getRequest(SimulationValues simulationValues, List<FootballClub> clubs) {
        return new CreateCompetitionRequestDTO("Competition",
                GB,
                "2024-07-01",
                "2025-04-30",
                new CompetitionParametersDTO(0, FootballClubTestUtils.getIdsOfSavedClubs(clubs), 0),
                simulationValues);
    }

    private static CreateCompetitionRequestDTO createRequest(int participants, List<Long> clubs) {
        return new CreateCompetitionRequestDTO("Competition",
                GB,
                "2024-07-01",
                "2025-04-30",
                new CompetitionParametersDTO(participants, clubs, 0),
                new SimulationValues(0.1, 0.2, 0.3));
    }

    public static Competition get(List<FootballClub> clubs) {
        var request = getRequest(clubs);

        return CompetitionCreator.createFromRequest(request, clubs).get();
    }
}
