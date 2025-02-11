package com.roumada.swiftscore.model.match;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FootballMatchCalculatedValues {
    private double homeSideCalculatedVictoryChance;
    private double awaySideCalculatedVictoryChance;
    private int extraVictorGoals;
}
