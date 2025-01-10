package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.logic.data.StandingsService;
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
@RequestMapping("/standings/")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService service;

    @GetMapping("{competitionId}")
    public ResponseEntity<Object> getStandingsForCompetition(@PathVariable long competitionId) {
        log.info(GET_ENDPOINT + "/standings/{}", competitionId);

        return service.getForCompetition(competitionId).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
