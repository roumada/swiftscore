package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.response.FootballMatchResponseDTO;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FootballMatchMapper {
    FootballMatchMapper INSTANCE = Mappers.getMapper(FootballMatchMapper.class);

    @Mapping(source = "homeSideFootballClub", target = "homeSide")
    @Mapping(source = "awaySideFootballClub", target = "awaySide")
    @Mapping(source = "homeSideStatistics.goalsScored", target = "homeSideGoalsScored")
    @Mapping(source = "awaySideStatistics.goalsScored", target = "awaySideGoalsScored")
    FootballMatchResponseDTO matchToMatchResponse(FootballMatch match);
}
