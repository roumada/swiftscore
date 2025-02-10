package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.criteria.SearchFootballClubSearchCriteriaDTO;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;
import com.roumada.swiftscore.service.FootballClubService;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubService service;

    @Operation(summary = "Find a football club")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Football club returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FootballClub.class))}),
            @ApiResponse(responseCode = "400", description = "Football club not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<Object> getFootballClub(@PathVariable long id,
                                                  HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = service.findById(id);
        return findResult.fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @Operation(summary = "Search for football clubs")
    @ApiResponse(responseCode = "200", description = "Football clubs returned",
            content = {@Content(mediaType = "application/json")})
    @GetMapping("/search")
    public ResponseEntity<Object> searchClubs(HttpServletRequest request,
                                              SearchFootballClubSearchCriteriaDTO criteria,
                                              Pageable pageable) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return ResponseEntity.ok(service.searchClubs(criteria, pageable));
    }

    @Operation(summary = "Create a football club")
    @ApiResponse(responseCode = "200", description = "Football club created",
            content = {@Content(mediaType = "application/json")})
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createFootballClub(@Valid @RequestBody CreateFootballClubRequestDTO dto,
                                                     HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return ResponseEntity.ok(service.save(dto));
    }

    @Operation(summary = "Update a football club")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Football club updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FootballClub.class))}),
            @ApiResponse(responseCode = "400", description = "Football club not found",
                    content = @Content)})
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<Object> updateFootballClub(@PathVariable long id,
                                                     @RequestBody CreateFootballClubRequestDTO dto,
                                                     HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.update(id, dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }
}
