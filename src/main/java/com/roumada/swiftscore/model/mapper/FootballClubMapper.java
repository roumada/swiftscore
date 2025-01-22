package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.FootballClubRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FootballClubMapper {
    FootballClubMapper INSTANCE = Mappers.getMapper(FootballClubMapper.class);

    FootballClub requestToObject(FootballClubRequestDTO dto);
    FootballClubRequestDTO objectToRequest(FootballClub footballClub);
}
