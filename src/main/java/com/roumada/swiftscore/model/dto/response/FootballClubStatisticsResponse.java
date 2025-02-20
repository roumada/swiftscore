package com.roumada.swiftscore.model.dto.response;

import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;

import java.util.List;

public record FootballClubStatisticsResponse(CreateFootballClubRequest footballClub,
                                             List<FootballMatchResponse> statistics) {
}
