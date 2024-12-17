package com.roumada.swiftscore.data.model.match;

import com.roumada.swiftscore.data.model.FootballClub;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("football_match_statistics")
public class FootballMatchStatistics {

    @Id
    private Long id = null;
    @DBRef
    private FootballClub footballClub;
    private int goalsScored = 0;

    public FootballMatchStatistics(FootballClub footballClub) {
        this.footballClub = footballClub;
    }

}
