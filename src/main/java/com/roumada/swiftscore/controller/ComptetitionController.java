package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.data.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.data.model.dto.CompetitionResponseDTO;
import com.roumada.swiftscore.data.mapper.CompetitionMapper;
import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competition")
@AllArgsConstructor
public class ComptetitionController {

    private final CompetitionDataLayer dataLayer;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<CompetitionResponseDTO> createCompetition(@RequestBody CompetitionRequestDTO dto) {
        var comp = dataLayer.generateAndSave(dto);
        return comp.map(competition ->
                        new ResponseEntity<>(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(comp.get()), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Competition> getCompetition(@PathVariable long id) {
        var comp = dataLayer.findCompetitionById(id);
        return comp.map(competition -> new ResponseEntity<>(comp.get(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new Competition(), HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/all")
    public List<Competition> getAllCompetitions() {
        return dataLayer.findAllComps();
    }
}
