package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.CompetitionDTO;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition")
@AllArgsConstructor
public class ComptetitionController {

    private final CompetitionDataLayer dataLayer;

    @PostMapping(consumes = "application/json")
    public long createCompetition(@RequestBody CompetitionDTO dto) {
        return dataLayer.persistWithClubIds(dto).getId();
    }

    @GetMapping("/{id}")
    public Competition getCompetition(@PathVariable long id){
        return dataLayer.getById(id);
    }
}
