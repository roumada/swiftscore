package com.roumada.swiftscore.model.organization.league;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("league")
@NoArgsConstructor
public class League {

    @Id
    Long id;
    private String name;
    private List<LeagueSeason> seasons;

    public League(String name, List<LeagueSeason> seasons) {
        this.name = name;
        this.seasons = seasons;
    }
}
