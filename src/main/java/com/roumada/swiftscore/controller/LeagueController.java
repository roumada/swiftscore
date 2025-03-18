package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.request.CreateLeagueRequest;
import com.roumada.swiftscore.model.organization.league.League;
import com.roumada.swiftscore.service.LeagueService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/league")
@AllArgsConstructor
public class LeagueController {

    private final LeagueService service;

    @Operation(summary = "Create a league")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "League created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = League.class))}),
            @ApiResponse(responseCode = "400", description = "League not created")})
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createLeague(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "League to create", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateLeagueRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                          "name": "English Football League",
                                          "countryCode": "GB",
                                          "startDate": "2020-07-01",
                                          "endDate": "2021-05-01",
                                          "competitions": [
                                              {
                                                  "name": "Premier Conference",
                                                  "country": "GB",
                                                  "simulationParameters": {
                                                    "variance": 0.35,
                                                    "scoreDifferenceDrawTrigger": 0.2,
                                                    "drawTriggerChance": 0.3
                                                  },
                                                  "competitionParameters": {
                                                    "participants": 8,
                                                    "relegationSpots": 6
                                                  }
                                              },
                                              {
                                                  "name": "Premier Conference 2",
                                                  "country": "GB",
                                                  "simulationParameters": {
                                                    "variance": 0.35,
                                                    "scoreDifferenceDrawTrigger": 0.2,
                                                    "drawTriggerChance": 0.3
                                                  },
                                                  "competitionParameters": {
                                                    "participants": 8,
                                                    "relegationSpots": 6
                                                  }
                                              }
                                          ]
                                      }
                                    """)))
            @Valid @RequestBody CreateLeagueRequest dto,
            HttpServletRequest request
    ) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.createFromRequest(dto).fold(
                errors -> ResponseEntity.badRequest().body(errors),
                ResponseEntity::ok
        );
    }

    @Operation(summary = "Simulate competitions within league with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "League returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = League.class))}),
            @ApiResponse(responseCode = "400", description = "League not found")})
    @PostMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id,
                                           @Parameter(description = "Amount of rounds to be simulated\n" +
                                                   "If value greater than rounds that can be simulated (and the league can still be simulated), " +
                                                   "simulates until the end", example = "1")
                                           @RequestParam @Min(1) Integer times,
                                           HttpServletRequest request){
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.simulate(id, times).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @Operation(summary = "Search for league with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "League returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = League.class))}),
            @ApiResponse(responseCode = "400", description = "League not found")})
    @GetMapping("/{id}")
    public ResponseEntity<Object> searchWithID(@PathVariable long id,
                                               HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @Operation(summary = "Delete league with ID")
    @ApiResponse(responseCode = "200")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id,
                                         HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
