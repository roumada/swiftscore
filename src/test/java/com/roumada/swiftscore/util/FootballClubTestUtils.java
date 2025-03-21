package com.roumada.swiftscore.util;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;

import java.util.ArrayList;
import java.util.List;

import static com.neovisionaries.i18n.CountryCode.*;

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
                FootballClub.builder().name("FC1").country(CountryCode.GB).stadiumName("FC1 Park").victoryChance(1).build(),
                FootballClub.builder().name("FC2").country(CountryCode.GB).stadiumName("FC2 Park").victoryChance(0.9f).build()
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

    public static List<FootballClub> getFourFootballClubsWithVariousCountries(){
        var clubs = getFourFootballClubs(false);
        clubs.get(1).setCountry(DE);
        clubs.get(2).setCountry(ES);
        clubs.get(3).setCountry(IT);

        return clubs;
    }

    public static List<FootballClub> getTenFootballClubs() {
        return List.of(
                FootballClub.builder().name("FC1").stadiumName("FC1 Park").country(CountryCode.GB).victoryChance(1).build(),
                FootballClub.builder().name("FC2").stadiumName("FC2 Park").country(CountryCode.GB).victoryChance(0.9f).build(),
                FootballClub.builder().name("FC3").stadiumName("FC3 Park").country(CountryCode.GB).victoryChance(0.8f).build(),
                FootballClub.builder().name("FC4").stadiumName("FC4 Park").country(CountryCode.GB).victoryChance(0.7f).build(),
                FootballClub.builder().name("FC5").stadiumName("FC5 Park").country(CountryCode.GB).victoryChance(0.6f).build(),
                FootballClub.builder().name("FC6").stadiumName("FC6 Park").country(CountryCode.GB).victoryChance(0.5f).build(),
                FootballClub.builder().name("FC7").stadiumName("FC7 Park").country(CountryCode.GB).victoryChance(0.4f).build(),
                FootballClub.builder().name("FC8").stadiumName("FC8 Park").country(CountryCode.GB).victoryChance(0.3f).build(),
                FootballClub.builder().name("FC9").stadiumName("FC9 Park").country(CountryCode.GB).victoryChance(0.2f).build(),
                FootballClub.builder().name("FC10").stadiumName("FC10 Park").country(CountryCode.GB).victoryChance(0.1f).build()
        );
    }

    public static List<FootballClub> getTenFootballClubsWithVariousCountries(){
        var clubs = getTenFootballClubs();

        clubs.get(2).setCountry(DE);
        clubs.get(3).setCountry(DE);
        clubs.get(4).setCountry(ES);
        clubs.get(5).setCountry(ES);
        clubs.get(6).setCountry(IT);
        clubs.get(7).setCountry(IT);

        return clubs;
    }

    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs){
        return getIdsOfSavedClubs(clubs, clubs.size());
    }

    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs, int amount){
        return new ArrayList<>(clubs.stream().map(FootballClub::getId).limit(amount).toList());
    }
}
