package com.roumada.swiftscore.model.match;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document("competition")
@CompoundIndex(def = "{'name': 1, 'country': 1}")
public class Competition {

    @Id
    private Long id = null;
    private int lastSimulatedRound = 0;
    private String name;
    private CountryCode country;
    private LocalDate startDate;
    private LocalDate endDate;
    private String season;
    private SimulationValues simulationValues;
    @DBRef
    private List<FootballClub> participants;
    @DBRef
    private List<CompetitionRound> rounds;

    @Builder
    private Competition(String name,
                        CountryCode country,
                        LocalDate startDate,
                        LocalDate endDate,
                        SimulationValues simulationValues,
                        List<FootballClub> participants,
                        List<CompetitionRound> rounds) {
        this.name = name;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simulationValues = simulationValues;
        this.participants = participants;
        this.rounds = rounds;
        determineSeason();
    }

    private void determineSeason() {
        if (startDate != null && endDate != null) {
            this.season = startDate.getYear() == endDate.getYear() ? String.valueOf(startDate.getYear()) :
                    "%s/%s".formatted(startDate.getYear(), endDate.getYear());
        }
    }

    public boolean isFullySimulated() {
        return rounds.size() <= (lastSimulatedRound);
    }

    public CompetitionRound currentRound() {
        return rounds.get(lastSimulatedRound);
    }

    public void incrementCurrentRoundNumber() {
        lastSimulatedRound++;
    }
}
