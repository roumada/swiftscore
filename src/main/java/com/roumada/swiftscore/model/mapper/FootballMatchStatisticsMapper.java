package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.dto.FootballMatchStatisticsDTO;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FootballMatchStatisticsMapper {
    FootballMatchStatisticsMapper INSTANCE = Mappers.getMapper(FootballMatchStatisticsMapper.class);

    FootballMatchStatisticsDTO statisticsToStatisticsDTO(FootballMatchStatistics statistics);
}
