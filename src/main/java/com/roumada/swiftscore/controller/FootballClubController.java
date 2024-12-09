package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubRepository repository;

    @PostMapping(consumes = "application/json")
    public long createFootballClub(@RequestBody FootballClubDTO dto){
        var fc = FootballClub.builder().name(dto.name()).victoryChance(dto.victoryChance()).build();
        return repository.save(fc).getId();
    }

    @GetMapping("/{id}")
    public FootballClub getFootballClub(@PathVariable long id){
        return repository.findById(id).orElse(FootballClub.builder().build());
    }
}
