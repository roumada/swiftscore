package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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

    @Operation(summary = "Find a football match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Football match returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FootballMatch.class))}),
            @ApiResponse(responseCode = "400", description = "Football match not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMatch(@PathVariable long id,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = dataLayer.findMatchById(id);
        return findResult.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Couldn't find football match with ID [%s]".formatted(id)));
    }
}
