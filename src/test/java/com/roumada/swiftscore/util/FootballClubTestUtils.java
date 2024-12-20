package com.roumada.swiftscore.util;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;

import java.util.List;

public class FootballClubTestUtils {

    public static List<FootballClub> getTenFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.9f).build(),
                FootballClub.builder().name("FC3").victoryChance(0.8f).build(),
                FootballClub.builder().name("FC4").victoryChance(0.7f).build(),
                FootballClub.builder().name("FC5").victoryChance(0.6f).build(),
                FootballClub.builder().name("FC6").victoryChance(0.5f).build(),
                FootballClub.builder().name("FC7").victoryChance(0.4f).build(),
                FootballClub.builder().name("FC8").victoryChance(0.3f).build(),
                FootballClub.builder().name("FC9").victoryChance(0.2f).build(),
                FootballClub.builder().name("FC10").victoryChance(0.1f).build()
        );
    }

    public static List<FootballClub> getFourFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.9f).build(),
                FootballClub.builder().name("FC3").victoryChance(0.8f).build(),
                FootballClub.builder().name("FC4").victoryChance(0.7f).build()
        );
    }

    public static Competition getTwoClubCompetition(){
        var participants = List.of(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.9f).build()
        );

        return Competition.builder()
                .participants(participants)
                .rounds(CompetitionRoundsGenerator.generate(participants).get())
                .variance(0.0)
                .build();
    }
}
