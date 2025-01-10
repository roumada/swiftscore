package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.mapper.CompetitionMapper;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.logic.data.CompetitionService;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.roumada.swiftscore.util.LogStringLiterals.GET_ENDPOINT;
import static com.roumada.swiftscore.util.LogStringLiterals.POST_ENDPOINT;

@Slf4j
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class ComptetitionController {

    private final CompetitionDataLayer dataLayer;
    private final CompetitionService competitionService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createCompetition(@RequestBody CompetitionRequestDTO dto) {
        log.debug(POST_ENDPOINT + " {} with request body {}", "/competition", dto);
        var result = dataLayer.generateAndSave(dto);
        return result.fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(success)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(@PathVariable long id) {
        log.info(GET_ENDPOINT + " /competition/{}", id);
        var comp = dataLayer.findCompetitionById(id);
        return comp
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest().body("Couldn't find competition with ID specified (%s)".formatted(id)));
    }

    @GetMapping("/all")
    public List<CompetitionResponseDTO> getAllCompetitions() {
        log.info(GET_ENDPOINT + " /competition/all");
        return dataLayer.findAllCompetitions().stream().map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO).toList();
    }


    @GetMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id) {
        log.info(GET_ENDPOINT + " /competition/{}/simulate", id);
        var competition = dataLayer.findCompetitionById(id);
        if (competition.isEmpty()) return ResponseEntity.badRequest()
                .body("Competition with ID [%s] not found.".formatted(id));

        var simulated = competitionService.simulateRound(competition.get());
        return simulated.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
