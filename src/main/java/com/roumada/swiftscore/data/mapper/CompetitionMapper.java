package com.roumada.swiftscore.data.mapper;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mapper
public interface CompetitionMapper {
    CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

    Logger log = LoggerFactory.getLogger(CompetitionMapper.class);

    @Mapping(source = "varianceType", target = "varianceType", qualifiedByName = "formatVarianceType")
    Competition competitionRequestDTOToCompetition(CompetitionRequestDTO request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "participants", target = "participantIds", qualifiedByName = "participantstoIds")
    @Mapping(source = "rounds", target = "roundIds", qualifiedByName = "roundsToIds")
    CompetitionResponseDTO competitionToCompetitionResponseDTO(Competition competition);

    @Named(value = "formatVarianceType")
    default Competition.VarianceType formatVarianceType(String varianceType) {
        Competition.VarianceType vt;
        try {
            vt = Competition.VarianceType.valueOf(varianceType.toUpperCase());
        } catch (Exception e) {
            log.warn("Couldn't recognize name of variance provided. Assuming no variance.");
            vt = Competition.VarianceType.NONE;
        }
        return vt;
    }

    @Named(value = "participantstoIds")
    default List<Long> participantstoIds(List<FootballClub> participants){
        return participants.stream().map(FootballClub::getId).toList();
    }

    @Named(value = "roundsToIds")
    default List<Long> roundsToIds(List<CompetitionRound> rounds){
        return rounds.stream().map(CompetitionRound::getId).toList();
    }
}
