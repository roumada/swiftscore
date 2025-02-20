package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/league")
@AllArgsConstructor
public class LeagueController {

    private final LeagueService service;

    @Operation(summary = "Create a league")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createLeague(
            @Valid @RequestBody CreateLeagueRequest dto,
            HttpServletRequest request
    ) {
        return service.createFromRequest(dto).fold(
                errors -> ResponseEntity.badRequest().body(errors),
                ResponseEntity::ok
        );
    }
}
