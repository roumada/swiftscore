package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponse;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = FootballMatchMapper.class)
public interface CompetitionRoundMapper {
    CompetitionRoundMapper INSTANCE = Mappers.getMapper(CompetitionRoundMapper.class);

    CompetitionRoundResponse roundToResponseDTO(CompetitionRound round);
    List<CompetitionRoundResponse> roundsToResponseDTOs(List<CompetitionRound> rounds);
}
