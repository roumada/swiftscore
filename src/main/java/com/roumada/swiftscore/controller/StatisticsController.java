package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.service.StatisticsService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/statistics/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("competition/{competitionId}")
    public ResponseEntity<Object> getStatisticsForCompetition(@PathVariable long competitionId,
                                                              HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));

        return service.getForCompetition(competitionId).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }

    @GetMapping("club/{clubId}")
    public ResponseEntity<Object> getStatisticsForClub(@PathVariable long clubId,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Boolean includeUnresolved,
                                                       HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        if (includeUnresolved == null) includeUnresolved = false;
        if (page == null) page = 0;

        return service.getForClub(clubId, page, includeUnresolved).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
