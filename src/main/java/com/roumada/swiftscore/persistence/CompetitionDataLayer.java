package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.unit.competition.schedule.CompetitionRoundsGenerator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.CompetitionDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.repository.CompetitionRepository;
import com.roumada.swiftscore.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class CompetitionDataLayer {

    private final CompetitionRepository competitionRepository;
    private final FootballClubRepository footballClubRepository;

    public Competition persistWithClubIds(CompetitionDTO dto) {
        var footballClubs = new ArrayList<FootballClub>();
        for(Long id : dto.participantIds()){
            footballClubs.add(footballClubRepository.findById(id).orElse(FootballClub.builder().build()));
        }

        var comp = CompetitionRoundsGenerator.generate(footballClubs);
        return competitionRepository.save(comp);
    }

    public Competition getById(Long id){
        return competitionRepository.findById(id).orElse(null);
    }
}
