package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Data
@Document("FootballMatchStatistics")
public class FootballMatchStatistics {
    private final FootballClub footballClub;
    @Setter
    private int goalsScored = 0;

    public FootballMatchStatistics(FootballClub footballClub){
        this.footballClub = footballClub;
    }

}
