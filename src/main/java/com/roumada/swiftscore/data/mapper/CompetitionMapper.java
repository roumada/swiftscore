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

import java.util.List;

@Mapper
public interface CompetitionMapper {
    CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

    @Mapping(source = "variance", target = "variance")
    Competition competitionRequestDTOToCompetition(CompetitionRequestDTO request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "participants", target = "participantIds", qualifiedByName = "participantstoIds")
    @Mapping(source = "rounds", target = "roundIds", qualifiedByName = "roundsToIds")
    CompetitionResponseDTO competitionToCompetitionResponseDTO(Competition competition);

    @Named(value = "participantstoIds")
    default List<Long> participantstoIds(List<FootballClub> participants) {
        return participants.stream().map(FootballClub::getId).toList();
    }

    @Named(value = "roundsToIds")
    default List<Long> roundsToIds(List<CompetitionRound> rounds) {
        return rounds.stream().map(CompetitionRound::getId).toList();
    }
}
