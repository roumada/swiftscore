package com.roumada.swiftscore.model.organization;

import java.util.List;

public record League(
        String name,
        String currentSeason,
        List<Long> competitionIds
) {
}
