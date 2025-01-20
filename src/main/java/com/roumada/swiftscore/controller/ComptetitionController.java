package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionMapper;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class ComptetitionController {

    private final CompetitionService service;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createCompetition(@Valid @RequestBody CompetitionRequestDTO dto,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.generateAndSave(dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(success)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(@PathVariable long id,
                                                 HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findCompetitionById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCompetition(@PathVariable long id,
                                                    @RequestBody CompetitionRequestDTO dto,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.update(id, dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCompetition(@PathVariable long id,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var comp = service.findCompetitionById(id);
        if(comp.isLeft()) return ResponseEntity.noContent().build();
        service.delete(id);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/all")
    public List<CompetitionResponseDTO> getAllCompetitions(HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findAllCompetitions().stream().map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO).toList();
    }


    @GetMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = service.findCompetitionById(id);
        if (findResult.isLeft()) return ResponseEntity.badRequest().body(findResult.getLeft());

        return service.simulateRound(findResult.get()).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
