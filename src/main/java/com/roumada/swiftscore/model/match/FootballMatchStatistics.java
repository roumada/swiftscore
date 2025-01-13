package com.roumada.swiftscore.model.match;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match_statistics")
public class FootballMatchStatistics {

    @Id
    private Long id = null;
    private Long competitionId;
    private Long footballMatchId;
    private Long footballClubId;
    private MatchStatisticsResult result = MatchStatisticsResult.UNFINISHED;
    private int goalsScored = 0;

    public FootballMatchStatistics(Long footballClubId) {
        this.footballClubId = footballClubId;
    }

    public enum MatchStatisticsResult {
        VICTORY, LOSS, DRAW, UNFINISHED
    }
}
