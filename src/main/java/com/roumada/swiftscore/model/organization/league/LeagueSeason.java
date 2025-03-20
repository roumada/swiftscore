package com.roumada.swiftscore.model.organization.league;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record LeagueSeason(String season,
                           List<Long> competitionIds) {
    public String nextSeason() {
        if(season.contains("/")) return Arrays.stream(season.split("/"))
                .map(s -> String.valueOf(Integer.parseInt(s) + 1))
                .collect(Collectors.joining("/"));
        else return String.valueOf(Integer.parseInt(season) + 1);
    }
}
