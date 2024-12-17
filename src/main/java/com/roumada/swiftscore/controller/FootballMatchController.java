package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/match")
@AllArgsConstructor
public class FootballMatchController {

    private final FootballMatchDataLayer dataLayer;

    @GetMapping("/{id}")
    public ResponseEntity<FootballMatch> getMatch(@PathVariable long id) {
        return dataLayer.findMatchById(id).map(match -> new ResponseEntity<>(match, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new FootballMatch(), HttpStatus.BAD_REQUEST));
    }
}
