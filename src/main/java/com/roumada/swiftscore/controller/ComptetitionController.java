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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class ComptetitionController {

    private final CompetitionService competitionService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createCompetition(@Valid @RequestBody CompetitionRequestDTO dto,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return competitionService.generateAndSave(dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(success)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(@PathVariable long id,
                                                 HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return competitionService.findCompetitionById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @GetMapping("/all")
    public List<CompetitionResponseDTO> getAllCompetitions(HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return competitionService.findAllCompetitions().stream().map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO).toList();
    }


    @GetMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = competitionService.findCompetitionById(id);
        if (findResult.isLeft()) return ResponseEntity.badRequest().body(findResult.getLeft());

        return competitionService.simulateRound(findResult.get()).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
