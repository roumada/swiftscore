package com.roumada.swiftscore.model.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("competition_round")
@AllArgsConstructor
@Builder
public class CompetitionRound {

    @Id
    private Long id;
    private int round;
    @DBRef
    private List<FootballMatch> matches;
}
