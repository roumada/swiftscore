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

    public static Competition getForPersistedClubs(List<FootballClub> clubs) {
        var request = new CreateCompetitionRequestDTO("Competition",
                GB,
                "2024-07-01",
                "2025-05-30",
                new CompetitionParametersDTO(0, FootballClubTestUtils.getIdsOfSavedClubs(clubs), 0),
                new SimulationValues(0.1, 0.2, 0.3));

        return CompetitionCreator.createFromRequest(request, clubs).get();
    }
}
