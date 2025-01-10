package com.roumada.swiftscore.model.dto;

import lombok.Getter;

@Getter
public class StandingsDTO {
    private final String footballClubName;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int goalsScored = 0;
    private int goalsConceded = 0;
    private int points = 0;

    public StandingsDTO(String footballClubName) {
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

    public void addPoints(int points) {
        this.points += points;
    }
}
