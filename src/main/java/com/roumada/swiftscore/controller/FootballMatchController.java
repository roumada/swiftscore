package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/match")
@AllArgsConstructor
public class FootballMatchController {

    private final FootballMatchDataLayer dataLayer;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMatch(@PathVariable long id) {
        log.info("Accessed GET endpoint {}", "/match/%s".formatted(id));
        var result = dataLayer.findMatchById(id);
        return result.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Couldn't find football match with ID [%s]".formatted(id)));
    }
}
