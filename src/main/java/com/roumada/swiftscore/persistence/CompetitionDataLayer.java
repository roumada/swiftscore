package com.roumada.swiftscore.persistence;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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

    public Page<Competition> findAllCompetitions(Pageable pageable) {
        return competitionRepository.findAll(pageable);
    }

    public void delete(long id) {
        competitionRepository.deleteById(id);
        log.debug("Competition with ID [{}] deleted.", id);
    }

    public Page<Competition> findByNameContaining(String name, Pageable pageable) {
        return competitionRepository.findByNameContaining(name, pageable);
    }

    public Page<Competition> findByCountry(CountryCode country, Pageable pageable) {
        return competitionRepository.findByCountry(country, pageable);
    }

    public Page<Competition> findByNameContainingIgnoreCaseAndCountry(String name, CountryCode country, Pageable pageable) {
        return competitionRepository.findByNameContainingIgnoreCaseAndCountry(name, country, pageable);
    }
}
