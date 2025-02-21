package com.roumada.swiftscore.persistence.datalayer;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class FootballMatchDataLayer {

    private FootballMatchRepository footballMatchRepository;

    public FootballMatch save(FootballMatch match) {
        return footballMatchRepository.save(match);
    }

    public void saveAll(Iterable<FootballMatch> matches) {
        footballMatchRepository.saveAll(matches);
    }

    public Optional<FootballMatch> findMatchById(long id) {
        return footballMatchRepository.findById(id);
    }

    public List<FootballMatch> findAllMatchesForClub(long footballClubId, int page, int size, boolean includeUnresolved) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("date")));

        Page<FootballMatch> pageResult = includeUnresolved ?
                footballMatchRepository
                        .findByFootballClubId(footballClubId, pageRequest) :
                footballMatchRepository.findByFootballClubIdExcludeUnfinished(footballClubId, pageRequest);

        return pageResult.getContent();
    }

    public List<FootballMatch> findAllMatchesForClubInCompetition(long competitionId, long footballClubId, int page, boolean includeUnresolved) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("date")));

        Page<FootballMatch> pageResult = includeUnresolved ?
                footballMatchRepository
                        .findByCompetitionIdAndFootballClubId(competitionId, footballClubId, pageRequest) :
                footballMatchRepository.findByCompetitionIdAndFootballClubIdExcludeUnfinished(competitionId, footballClubId, pageRequest);

        return pageResult.getContent();
    }

    public void deleteByCompetitionId(long id) {
        footballMatchRepository.deleteByCompetitionId(id);
    }
}
