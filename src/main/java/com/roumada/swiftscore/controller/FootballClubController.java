package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubDataLayer dataLayer;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<FootballClub> createFootballClub(@RequestBody FootballClubDTO dto) {
        var fc = dataLayer.save(FootballClub.builder().name(dto.name()).victoryChance(dto.victoryChance()).build());
        return new ResponseEntity<>(fc, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FootballClub> getFootballClub(@PathVariable long id) {
        var fc = dataLayer.findById(id);
        return fc.map(footballClub -> new ResponseEntity<>(footballClub, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(FootballClub.builder().build(), HttpStatus.BAD_REQUEST));
    }
}
