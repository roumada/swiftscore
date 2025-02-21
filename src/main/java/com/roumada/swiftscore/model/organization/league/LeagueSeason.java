package com.roumada.swiftscore.model.organization.league;

import java.util.List;

public record LeagueSeason(String season,
                           List<Long> competitionIds) {
}
