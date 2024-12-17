package com.roumada.swiftscore.util;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.logic.competition.CompetitionRoundsGenerator;

import java.util.List;

public class FootballClubTestUtils {

    public static List<FootballClub> getTenFootballClubs() {
        return List.of(
                FootballClub.builder().id(1L).name("FC1").victoryChance(1).build(),
                FootballClub.builder().id(2L).name("FC2").victoryChance(0.9f).build(),
                FootballClub.builder().id(3L).name("FC3").victoryChance(0.8f).build(),
                FootballClub.builder().id(4L).name("FC4").victoryChance(0.7f).build(),
                FootballClub.builder().id(5L).name("FC5").victoryChance(0.6f).build(),
                FootballClub.builder().id(6L).name("FC6").victoryChance(0.5f).build(),
                FootballClub.builder().id(7L).name("FC7").victoryChance(0.4f).build(),
                FootballClub.builder().id(8L).name("FC8").victoryChance(0.3f).build(),
                FootballClub.builder().id(9L).name("FC9").victoryChance(0.2f).build(),
                FootballClub.builder().id(10L).name("FC10").victoryChance(0.1f).build()
        );
    }

    public static List<FootballClub> getFourFootballClubs() {
        return List.of(
                FootballClub.builder().id(1L).name("FC1").victoryChance(1).build(),
                FootballClub.builder().id(2L).name("FC2").victoryChance(0.9f).build(),
                FootballClub.builder().id(3L).name("FC3").victoryChance(0.8f).build(),
                FootballClub.builder().id(4L).name("FC4").victoryChance(0.7f).build()
        );
    }

    public static Competition getTwoClubCompetition(){
        var participants = List.of(
                FootballClub.builder().id(1L).name("FC1").victoryChance(1).build(),
                FootballClub.builder().id(2L).name("FC2").victoryChance(0.9f).build()
        );

        return Competition.builder()
                .participants(participants)
                .rounds(CompetitionRoundsGenerator.generate(participants))
                .variance(0.0f)
                .build();
    }
}
