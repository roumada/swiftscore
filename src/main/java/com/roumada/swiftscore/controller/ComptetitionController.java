package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.logic.data.CompetitionService;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionMapper;
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
    public ResponseEntity<Object> createCompetition(HttpServletRequest request, @Valid @RequestBody CompetitionRequestDTO dto) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        var result = competitionService.generateAndSave(dto);
        return result.fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(success)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(HttpServletRequest request, @PathVariable long id) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var eitherCompetition = competitionService.findCompetitionById(id);
        return eitherCompetition.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @GetMapping("/all")
    public List<CompetitionResponseDTO> getAllCompetitions(HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return competitionService.findAllCompetitions().stream().map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO).toList();
    }


    @GetMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(HttpServletRequest request, @PathVariable long id) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var competition = competitionService.findCompetitionById(id);
        if (competition.isLeft()) return ResponseEntity.badRequest().body(competition.getLeft());

        var simulated = competitionService.simulateRound(competition.get());
        return simulated.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
