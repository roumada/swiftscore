package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.CompetitionDTO;
import com.roumada.swiftscore.model.match.Competition;
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
    public ResponseEntity<Long> createCompetition(@RequestBody CompetitionDTO dto) {
        var comp = dataLayer.generateAndSave(dto.participantIds());
        return comp == null ?
                new ResponseEntity<>(-1L, HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>(comp.getId(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Competition> getAllCompetitions() {
        return dataLayer.findAllComps();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Competition> getCompetition(@PathVariable long id) {
        var comp = dataLayer.findCompetitionById(id);
        return comp.map(competition -> new ResponseEntity<>(comp.get(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new Competition(), HttpStatus.BAD_REQUEST));
    }
}
