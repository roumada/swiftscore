package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.data.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubDataLayer dataLayer;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFootballClub(@PathVariable long id) {
        var findResult = dataLayer.findById(id);
        return findResult.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Couldn't find football club with ID [%s]".formatted(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllClubs() {
        return ResponseEntity.ok(dataLayer.findAll());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createFootballClub(@RequestBody FootballClubDTO dto) {
        var saveResult = dataLayer.saveFromDto(dto);
        return saveResult.fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(success)
        );
    }


}
