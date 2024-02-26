package com.roumada.swiftscore.model.match;

import java.util.List;

public record MatchWeek(int matchWeekNumber, List<FootballMatch> matches) {
}
