package com.roumada.swiftscore.model.dto;

import java.util.List;

public record FootballClubStatisticsDTO(FootballClubDTO footballClub,
                                        List<FootballMatchStatisticsDTO> statistics) {
}
