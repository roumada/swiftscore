package com.roumada.swiftscore.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FootballClubDTO {
    private String name;
    private float victoryChance;
}
