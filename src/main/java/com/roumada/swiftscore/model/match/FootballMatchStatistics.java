package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("FootballMatchStatistics")
public class FootballMatchStatistics {
    private FootballClub footballClub;
    private int goalsScored = 0;

    public FootballMatchStatistics(FootballClub footballClub){
        this.footballClub = footballClub;
    }
}
