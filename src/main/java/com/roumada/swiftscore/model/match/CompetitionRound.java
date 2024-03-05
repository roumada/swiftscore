package com.roumada.swiftscore.model.match;

import java.util.List;

public record CompetitionRound(int competitionRoundNumber, List<FootballMatch> matches) {
}
