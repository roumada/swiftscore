package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.response.CompetitionRoundResponseDTO;
import com.roumada.swiftscore.model.match.CompetitionRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = FootballMatchMapper.class)
public interface CompetitionRoundMapper {
    CompetitionRoundMapper INSTANCE = Mappers.getMapper(CompetitionRoundMapper.class);

    @Mapping(source = "matches", target = "matches")
    CompetitionRoundResponseDTO roundToResponseDTO(CompetitionRound round);
}
