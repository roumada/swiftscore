package com.roumada.swiftscore.model.match;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document("competition")
public class Competition {

    @Id
    private Long id = null;
    private int lastSimulatedRound = 0;
    private String name;
    private CountryCode country;
    private LocalDate startDate;
    private LocalDate endDate;
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
    }

    public boolean canSimulate(int times) {
        return rounds.size() >= (lastSimulatedRound + times);
    }

    public CompetitionRound currentRound() {
        return rounds.get(lastSimulatedRound);
    }

    public void incrementCurrentRoundNumber() {
        lastSimulatedRound++;
    }
}
