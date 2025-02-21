package com.roumada.swiftscore.model.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class FootballClubStandings {
    private final String footballClubName;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int goalsScored = 0;
    private int goalsConceded = 0;
    private int goalDifference = 0;
    private int points = 0;
    private List<FootballMatchResponse> lastMatchesStatistics;

    public FootballClubStandings(String footballClubName) {
        this.footballClubName = footballClubName;
    }

    public void addWin() {
        wins++;
        points += 3;
    }

    public void addDraw() {
        draws++;
        points += 1;
    }

    public void addLoss() {
        losses++;
    }

    public void addGoalsScored(int goals) {
        goalsScored += goals;
    }

    public void addGoalsConceded(int goals) {
        goalsConceded += goals;
    }

    public void setStatistics(List<FootballMatchResponse> statistics) {
        this.lastMatchesStatistics = statistics;
    }

    public void calculateGoalDifference(){
        goalDifference = goalsScored - goalsConceded;
    }
}
