package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.response.FootballMatchResponse;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FootballMatchMapper {
    FootballMatchMapper INSTANCE = Mappers.getMapper(FootballMatchMapper.class);

    @Mapping(source = "homeSideFootballClub", target = "homeSide")
    @Mapping(source = "awaySideFootballClub", target = "awaySide")
    FootballMatchResponse matchToMatchResponse(FootballMatch match);
}
