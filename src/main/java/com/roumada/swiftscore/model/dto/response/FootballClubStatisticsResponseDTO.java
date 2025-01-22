package com.roumada.swiftscore.model.dto.response;

import com.roumada.swiftscore.model.dto.request.FootballClubRequestDTO;

import java.util.List;

public record FootballClubStatisticsResponseDTO(FootballClubRequestDTO footballClub,
                                                List<FootballMatchStatisticsResponseDTO> statistics) {
}
