package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.repository.FootballClubRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubRepository fcr;

    @PostMapping(consumes = "application/json")
    public String createFootballClub(@RequestBody FootballClub footballClub){
        return fcr.save(footballClub).getName();
    }

    @GetMapping("/{name}")
    public FootballClub getFootballClub(@PathVariable String name){
        return fcr.findByName(name).orElse(FootballClub.builder().build());
    }
}
