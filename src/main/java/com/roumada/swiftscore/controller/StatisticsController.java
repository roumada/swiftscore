package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.logic.data.StatisticsService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Object> getStatisticsForCompetition(HttpServletRequest request, @PathVariable long competitionId) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));

        return service.getForCompetition(competitionId).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }

    @GetMapping("club/{clubId}")
    public ResponseEntity<Object> getStatisticsForClub(HttpServletRequest request,
                                                       @PathVariable long clubId,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Boolean includeUnresolved) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        if(includeUnresolved == null) includeUnresolved = false;
        if(page == null) page = 0;

        return service.getForClub(clubId, page, includeUnresolved).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
