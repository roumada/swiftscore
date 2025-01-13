package com.roumada.swiftscore.model.match;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match_statistics")
public class FootballMatchStatistics {

    @Id
    private Long id = null;
    private Long footballMatchId;
    private Long footballClubId;
    private MatchStatisticsResult result = MatchStatisticsResult.UNFINISHED;
    private int goalsScored = 0;

    FootballMatchStatistics(Long id) {
        this.footballClubId = id;
    }

    public enum MatchStatisticsResult {
        VICTORY, LOSS, DRAW, UNFINISHED
    }
}
