package com.roumada.swiftscore.model.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("competition_round")
@AllArgsConstructor
public class CompetitionRound {

    @Id
    private long id;
    private int round;
    private List<FootballMatch> matches;
}
