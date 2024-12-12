package com.roumada.swiftscore.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roumada.swiftscore.model.match.Competition;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionDTO(
        Long id,
        List<Long> participantIds,
        List<Long> matchWeekIds,
        Competition.VarianceType variance

) {
}
