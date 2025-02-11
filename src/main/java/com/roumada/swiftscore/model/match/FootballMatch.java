package com.roumada.swiftscore.model.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("football_match")
@NoArgsConstructor
public class FootballMatch {

    @Id
    private Long id = null;
    private LocalDateTime date;
    private Long competitionId;
    private Long competitionRoundId;
    @DBRef
    private FootballClub homeSideFootballClub;
    private int homeSideGoalsScored;
    @DBRef
    private FootballClub awaySideFootballClub;
    private int awaySideGoalsScored;
    private MatchResult matchResult = MatchResult.UNFINISHED;
    private FootballMatchCalculatedValues calculatedValues = new FootballMatchCalculatedValues();

    public FootballMatch(LocalDateTime date, FootballClub homeSideFootballClub, FootballClub awaySideFootballClub) {
        this.date = date;
        this.homeSideFootballClub = homeSideFootballClub;
        this.awaySideFootballClub = awaySideFootballClub;
    }

    public FootballMatch(FootballClub homeSideFootballClub, FootballClub awaySideFootballClub) {
        this(null, homeSideFootballClub, awaySideFootballClub);
    }

    @JsonIgnore
    public double getHomeSideVictoryChance() {
        return homeSideFootballClub.getVictoryChance();
    }

    @JsonIgnore
    public double getAwaySideVictoryChance() {
        return awaySideFootballClub.getVictoryChance();
    }

    public void setHomeSideCalculatedVictoryChance(double homeSideVictoryChance) {
        calculatedValues.setHomeSideCalculatedVictoryChance(homeSideVictoryChance);
    }

    public void setAwaySideCalculatedVictoryChance(double awaySideVictoryChance) {
        calculatedValues.setAwaySideCalculatedVictoryChance(awaySideVictoryChance);
    }

    @JsonIgnore
    public double getHomeSideCalculatedVictoryChance() {
        return calculatedValues.getHomeSideCalculatedVictoryChance();
    }

    @JsonIgnore
    public double getAwaySideCalculatedVictoryChance() {
        return calculatedValues.getAwaySideCalculatedVictoryChance();
    }

    public void setExtraVictorGoals(int addedGoals) {
        calculatedValues.setExtraVictorGoals(addedGoals);
    }

    @JsonIgnore
    public int getExtraVictorGoals() {
        return calculatedValues.getExtraVictorGoals();
    }

    public enum MatchResult {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
