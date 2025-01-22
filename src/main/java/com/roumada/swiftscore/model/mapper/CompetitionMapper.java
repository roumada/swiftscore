package com.roumada.swiftscore.model.mapper;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.response.CompetitionResponseDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompetitionMapper {
    CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "participants", target = "participantIds", qualifiedByName = "participantsToIds")
    @Mapping(source = "rounds", target = "roundIds", qualifiedByName = "roundsToIds")
    @Mapping(source = "currentRoundNumber", target = "currentRound")
    CompetitionResponseDTO competitionToCompetitionResponseDTO(Competition competition);

    @Named(value = "participantsToIds")
    default List<Long> participantsToIds(List<FootballClub> participants) {
        return participants.stream().map(FootballClub::getId).toList();
    }

    @Named(value = "roundsToIds")
    default List<Long> roundsToIds(List<CompetitionRound> rounds) {
        return rounds.stream().map(CompetitionRound::getId).toList();
    }
}
