package com.roumada.swiftscore.model.match;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("football_match_statistics")
@NoArgsConstructor
public class FootballMatchStatistics {

    @Id
    private Long id = null;
    private LocalDateTime date;
    private Long competitionId;
    private Long footballMatchId;
    private Long footballClubId;
    private MatchStatisticsResult result = MatchStatisticsResult.UNFINISHED;
    private int goalsScored = 0;
    private int opponentGoalsScored = 0;

    public FootballMatchStatistics(Long footballClubId) {
        this(footballClubId, null);
    }

    public FootballMatchStatistics(Long footballClubId, LocalDateTime date) {
        this.date = date;
        this.footballClubId = footballClubId;
    }

    public enum MatchStatisticsResult {
        VICTORY, LOSS, DRAW, UNFINISHED
    }
}
