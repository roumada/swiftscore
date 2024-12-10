package com.roumada.swiftscore.model.match;

import com.roumada.swiftscore.model.FootballClub;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Data
@Document("football_match_statistics")
public class FootballMatchStatistics {

    @Id
    private Long id;
    private final FootballClub footballClub;
    @Setter
    private int goalsScored = 0;

    public FootballMatchStatistics(FootballClub footballClub){
        this.footballClub = footballClub;
    }

}
