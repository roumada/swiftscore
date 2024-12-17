package com.roumada.swiftscore.data.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record CompetitionResponseDTO(
        Long id,
        int currentRound,
        List<Long> participantIds,
        List<Long> roundIds
) { }
