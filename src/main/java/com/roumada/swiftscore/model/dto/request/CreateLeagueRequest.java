package com.roumada.swiftscore.model.dto.request;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.dto.StartEndStringDates;
import com.roumada.swiftscore.validation.annotation.ValidCreateLeagueRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@ValidCreateLeagueRequest
public record CreateLeagueRequest(
        @NotEmpty(message = "League name must not be empty")
        String name,
        @NotNull(message = "Country code must not be empty")
        CountryCode countryCode,
        @NotEmpty(message = "Start date must not be empty")
        String startDate,
        @NotEmpty(message = "End date must not be empty")
        String endDate,
        @Size(min = 2, message = "League must have at least two competition defined")
        List<CreateLeagueCompetitionRequest> competitions
) implements StartEndStringDates {
}
