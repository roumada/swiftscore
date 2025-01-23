package com.roumada.swiftscore.persistence;

import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class CompetitionDataLayer {

    private final CompetitionRepository competitionRepository;

    public Competition save(Competition competition) {
        var saved = competitionRepository.save(competition);
        log.debug("Competition with ID [{}] saved.", saved.getId());
        return saved;
    }

    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> findAllCompetitions() {
        return competitionRepository.findAll();
    }

    public void delete(long id) {
        competitionRepository.deleteById(id);
        log.debug("Competition with ID [{}] deleted.", id);
    }
}
