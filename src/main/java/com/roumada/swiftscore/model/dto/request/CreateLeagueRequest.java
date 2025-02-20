package com.roumada.swiftscore.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateLeagueRequest(
        @NotEmpty(message = "League name must not be empty")
        String name,
        @NotEmpty(message = "Start date must not be empty")
        String startDate,
        @NotEmpty(message = "End date must not be empty")
        String endDate,
        @Size(min = 2, message = "League must have at least two competition defined")
        List<CreateCompetitionRequest> competitions
) {
}
