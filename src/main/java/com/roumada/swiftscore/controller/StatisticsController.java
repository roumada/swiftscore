package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.logic.data.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("club/{clubId}")
    public ResponseEntity<Object> getStatisticsForClub(@PathVariable long clubId,
                                                       @RequestParam int page,
                                                       @RequestParam(required = false) Boolean includeUnresolved) {
        log.info(GET_ENDPOINT + "/statistics/club/{}", clubId);
        if(includeUnresolved == null) includeUnresolved = false;

        return service.getForClub(clubId, page, includeUnresolved).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
