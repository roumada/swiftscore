package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponseDTO;
import com.roumada.swiftscore.model.match.CompetitionRound;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = FootballMatchMapper.class)
public interface CompetitionRoundMapper {
    CompetitionRoundMapper INSTANCE = Mappers.getMapper(CompetitionRoundMapper.class);

    CompetitionRoundResponseDTO roundToResponseDTO(CompetitionRound round);
    List<CompetitionRoundResponseDTO> roundsToResponseDTOs(List<CompetitionRound> rounds);
}
