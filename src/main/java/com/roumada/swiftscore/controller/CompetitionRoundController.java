package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.service.CompetitionRoundService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/competition/round/")
@RequiredArgsConstructor
public class CompetitionRoundController {

    private final CompetitionRoundService service;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRound(@PathVariable long id,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }
}
