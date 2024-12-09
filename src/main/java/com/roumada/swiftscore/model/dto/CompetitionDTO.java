package com.roumada.swiftscore.model.dto;

import com.roumada.swiftscore.model.match.CompetitionRound;

import java.util.List;

public record CompetitionDTO(int round, List<CompetitionRound> rounds) {
}
