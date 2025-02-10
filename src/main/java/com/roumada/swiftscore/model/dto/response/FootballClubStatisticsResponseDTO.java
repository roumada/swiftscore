package com.roumada.swiftscore.model.dto.response;

import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;

import java.util.List;

public record FootballClubStatisticsResponseDTO(CreateFootballClubRequestDTO footballClub,
                                                List<FootballMatchResponseDTO> statistics) {
}
