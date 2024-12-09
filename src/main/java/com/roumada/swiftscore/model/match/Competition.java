package com.roumada.swiftscore.model.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Competition")
@NoArgsConstructor
@AllArgsConstructor
public class Competition {

    @Id
    private long id;
    private int currentRound = 1;
    private List<CompetitionRound> rounds;

}
