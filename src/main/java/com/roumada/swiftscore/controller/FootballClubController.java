package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.roumada.swiftscore.util.LogStringLiterals.GET_ENDPOINT;
import static com.roumada.swiftscore.util.LogStringLiterals.POST_ENDPOINT;

@Slf4j
@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubDataLayer dataLayer;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFootballClub(@PathVariable long id) {
        log.info(GET_ENDPOINT + " /footballclub/{}", id);
        var findResult = dataLayer.findById(id);
        return findResult.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Couldn't find football club with ID [%s]".formatted(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllClubs() {
        log.info(GET_ENDPOINT + " /footballclub/all");
        return ResponseEntity.ok(dataLayer.findAll());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createFootballClub(@RequestBody FootballClubDTO dto) {
        log.debug(POST_ENDPOINT + " {} with request body {}", "/footballclub", dto);
        var saveResult = dataLayer.saveFromDto(dto);
        return saveResult.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
