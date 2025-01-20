package com.roumada.swiftscore.util;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;

import java.util.List;

public class FootballClubTestUtils {
    public static FootballClub getClub() {
        return FootballClub.builder()
                .name("FC1")
                .country(CountryCode.GB)
                .stadiumName("FC1 Stadium")
                .victoryChance(0.5)
                .build();
    }

    public static List<FootballClub> getTwoFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.9f).build()
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
}
