package com.roumada.swiftscore.persistence;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballClubDataLayer {

    private final FootballClubRepository repository;

    public FootballClub save(FootballClub footballClub) {
        var saved = repository.save(footballClub);
        log.debug("Football club with data [{}] saved.", footballClub);
        return saved;
    }

    public List<FootballClub> saveAll(List<FootballClub> fcs) {
        return repository.saveAll(fcs);
    }

    public Optional<FootballClub> findById(long id) {
        return repository.findById(id);
    }

    public List<FootballClub> findAllByIdAndCountry(List<Long> longs, CountryCode country) {
        return repository.findAllByIdInAndCountryIn(longs, country);
    }

    public List<FootballClub> findByIdNotInAndCountryIn(List<Long> footballClubIds, CountryCode country, int amount) {
        var pageable = PageRequest.of(0, amount);
        return repository.findByIdNotInAndCountryIn(footballClubIds, country, pageable);
    }
}
