package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.data.mapper.CompetitionMapper;
import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.logic.competition.CompetitionService;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class ComptetitionController {

    private final CompetitionDataLayer dataLayer;
    private final CompetitionService competitionService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createCompetition(@RequestBody CompetitionRequestDTO dto) {
        var result = dataLayer.generateAndSave(dto);
        return result
                .<ResponseEntity<Object>>map(res ->
                        ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(res)))
                .orElseGet(() ->
                        ResponseEntity.badRequest().body("Couldn't create competition. Check validity of provided club IDs"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(@PathVariable long id) {
        var comp = dataLayer.findCompetitionById(id);
        return comp
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest().body("Couldn't find competition with ID specified (%s)".formatted(id)));
    }

    @GetMapping("/all")
    public List<CompetitionResponseDTO> getAllCompetitions() {
        return dataLayer.findAllComps().stream().map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO).toList();
    }


    @GetMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id) {
        var competition = dataLayer.findCompetitionById(id);
        if (competition.isEmpty()) return ResponseEntity.badRequest()
                .body("Competition with ID [%s] not found.".formatted(id));

        var simulated = competitionService.simulateRound(competition.get());
        return simulated.fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(success)
        );
    }
}
