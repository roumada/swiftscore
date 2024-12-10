package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.MonoPair;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/match")
@AllArgsConstructor
public class FootballMatchController {

    @GetMapping("/{id}")
    public ResponseEntity<FootballMatch> getMatch(@PathVariable long id) {
        return null;
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<MonoPair<FootballMatchStatistics>> getMatchStatistics(@PathVariable long id) {
        return null;
    }
}
