package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FootballClubMapper {
    FootballClubMapper INSTANCE = Mappers.getMapper(FootballClubMapper.class);

    FootballClub requestToObject(CreateFootballClubRequest dto);

    CreateFootballClubRequest objectToRequest(FootballClub footballClub);
}
