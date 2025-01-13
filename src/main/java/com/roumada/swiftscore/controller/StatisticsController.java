package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.logic.data.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.roumada.swiftscore.util.LogStringLiterals.GET_ENDPOINT;

@Slf4j
@RestController
@RequestMapping("/statistics/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("competition/{competitionId}")
    public ResponseEntity<Object> getStatisticsForCompetition(@PathVariable long competitionId) {
        log.info(GET_ENDPOINT + "/statistics/competition/{}", competitionId);

        return service.getForCompetition(competitionId).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }

    @GetMapping("club/{clubId}/{page}")
    public ResponseEntity<Object> getStatisticsForClub(@PathVariable long clubId, @PathVariable int page) {
        log.info(GET_ENDPOINT + "/statistics/club/{}", clubId);

        return service.getForClub(clubId, page).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
