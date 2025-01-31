package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.service.CompetitionRoundService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/competition/round/")
@RequiredArgsConstructor
public class CompetitionRoundController {

    private final CompetitionRoundService service;

    @Operation(summary = "Find a competition round")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition round returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompetitionRound.class))}),
            @ApiResponse(responseCode = "400", description = "Competition round not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRound(@PathVariable long id,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }
}
