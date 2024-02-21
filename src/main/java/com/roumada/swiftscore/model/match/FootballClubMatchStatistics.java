package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;

@Data
public class FootballClubMatchStatistics {
    private FootballClub footballClub;
    private int goalsScored = 0;

    public FootballClubMatchStatistics(FootballClub footballClub){
        this.footballClub = footballClub;
    }
}
