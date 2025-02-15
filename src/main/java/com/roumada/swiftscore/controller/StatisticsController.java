package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.response.FootballClubStatisticsResponseDTO;
import com.roumada.swiftscore.model.dto.response.FootballClubStandings;
import com.roumada.swiftscore.service.StatisticsService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/statistics/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @Operation(summary = "Get standings for competition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standings generated and returned",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FootballClubStandings.class)))}),
            @ApiResponse(responseCode = "400", description = "Competition not found",
                    content = @Content)})
    @GetMapping("competition/{competitionId}")
    public ResponseEntity<Object> getStatisticsForCompetition(@PathVariable long competitionId,
                                                              @RequestParam(required = false, defaultValue = "false") Boolean simplify,
                                                              HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));

        return service.getForCompetition(competitionId, simplify).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }

    @Operation(summary = "Get statistics of last matches played for club")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standings generated and returned",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FootballClubStatisticsResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Club not found",
                    content = @Content)})
    @GetMapping("club/{clubId}")
    public ResponseEntity<Object> getStatisticsForClub(@PathVariable long clubId,
                                                       @Parameter(description = "Page number", example = "0")
                                                       @RequestParam(required = false, defaultValue = "0") Integer page,
                                                       @Parameter(description = "Page size", example = "0")
                                                       @RequestParam(required = false, defaultValue = "5") Integer size,
                                                       @Parameter(description = "Whether to include planned but not yet played matches or not")
                                                       @RequestParam(required = false) Boolean includeUnresolved,
                                                       HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        if (includeUnresolved == null) includeUnresolved = false;
        if (page == null) page = 0;

        return service.getForClub(clubId, page, size, includeUnresolved).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok
        );
    }
}
