package com.roumada.swiftscore.util;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;

import java.util.ArrayList;
import java.util.List;

public class FootballClubTestUtils {
    public static FootballClub getClub(boolean setId) {
        var fc = FootballClub.builder()
                .name("FC1")
                .country(CountryCode.GB)
                .stadiumName("FC1 Stadium")
                .victoryChance(0.5)
                .build();
        if (setId) fc.setId(1L);
        return fc;
    }

    public static List<FootballClub> getTwoFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").stadiumName("FC1 Park").victoryChance(1).build(),
                FootballClub.builder().name("FC2").stadiumName("FC2 Park").victoryChance(0.9f).build()
        );
    }

    public static List<FootballClub> getFourFootballClubs(boolean setIds) {
        List<FootballClub> clubs = new ArrayList<>();
        clubs.add(FootballClub.builder().name("FC1").country(CountryCode.GB).victoryChance(1).build());
        clubs.add(FootballClub.builder().name("FC2").country(CountryCode.GB).victoryChance(0.9).build());
        clubs.add(FootballClub.builder().name("FC3").country(CountryCode.GB).victoryChance(0.8).build());
        clubs.add(FootballClub.builder().name("FC4").country(CountryCode.GB).victoryChance(0.7).build());

        if (setIds) {
            clubs.get(0).setId(0L);
            clubs.get(1).setId(1L);
            clubs.get(2).setId(2L);
            clubs.get(3).setId(3L);
        }

        return clubs;
    }

    public static List<FootballClub> getTenFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").country(CountryCode.GB).victoryChance(1).build(),
                FootballClub.builder().name("FC2").country(CountryCode.GB).victoryChance(0.9f).build(),
                FootballClub.builder().name("FC3").country(CountryCode.GB).victoryChance(0.8f).build(),
                FootballClub.builder().name("FC4").country(CountryCode.GB).victoryChance(0.7f).build(),
                FootballClub.builder().name("FC5").country(CountryCode.GB).victoryChance(0.6f).build(),
                FootballClub.builder().name("FC6").country(CountryCode.GB).victoryChance(0.5f).build(),
                FootballClub.builder().name("FC7").country(CountryCode.GB).victoryChance(0.4f).build(),
                FootballClub.builder().name("FC8").country(CountryCode.GB).victoryChance(0.3f).build(),
                FootballClub.builder().name("FC9").country(CountryCode.GB).victoryChance(0.2f).build(),
                FootballClub.builder().name("FC10").country(CountryCode.GB).victoryChance(0.1f).build()
        );
    }

    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs){
        return getIdsOfSavedClubs(clubs, clubs.size());
    }

    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs, int amount){
        return new ArrayList<>(clubs.stream().map(FootballClub::getId).limit(amount).toList());
    }
}
