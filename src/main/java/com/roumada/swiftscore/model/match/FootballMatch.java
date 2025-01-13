package com.roumada.swiftscore.model.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.MonoPair;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match")
public class FootballMatch {

    @Id
    private Long id = null;
    private Long competitionId;
    private Long competitionRoundId;
    @DBRef
    private FootballClub homeSideFootballClub;
    @DBRef
    private FootballClub awaySideFootballClub;
    @DBRef
    private FootballMatchStatistics homeSideStatistics;
    @DBRef
    private FootballMatchStatistics awaySideStatistics;
    private MatchResult matchResult = MatchResult.UNFINISHED;

    public FootballMatch(FootballClub homeSideFootballClub, FootballClub awaySideFootballClub) {
        this.homeSideFootballClub = homeSideFootballClub;
        this.awaySideFootballClub = awaySideFootballClub;
        homeSideStatistics = new FootballMatchStatistics(homeSideFootballClub.getId());
        awaySideStatistics = new FootballMatchStatistics(awaySideFootballClub.getId());
    }

    public double getHomeSideVictoryChance() {
        return homeSideFootballClub.getVictoryChance();
    }

    public double getAwaySideVictoryChance() {
        return awaySideFootballClub.getVictoryChance();
    }

    @JsonIgnore
    public MonoPair<FootballMatchStatistics> getStatistics() {
        return MonoPair.of(homeSideStatistics, awaySideStatistics);
    }

    public enum MatchResult {
        HOME_SIDE_VICTORY, AWAY_SIDE_VICTORY, DRAW, UNFINISHED
    }
}
