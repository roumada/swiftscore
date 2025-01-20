package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.service.FootballClubService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubService service;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFootballClub(@PathVariable long id,
                                                  HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = service.findById(id);
        return findResult.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllClubs(HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createFootballClub(@Valid @RequestBody FootballClubDTO dto,
                                                     HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return ResponseEntity.ok(service.save(dto));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<Object> updateFootballClub(@PathVariable long id,
                                                     @RequestBody FootballClubDTO dto,
                                                     HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.update(id, dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }
}
