package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.criteria.SearchCompetitionCriteriaDTO;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.UpdateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionResponseDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionSimulationResponseDTO;
import com.roumada.swiftscore.model.dto.response.CompetitionSimulationSimpleResponseDTO;
import com.roumada.swiftscore.model.mapper.CompetitionMapper;
import com.roumada.swiftscore.model.mapper.CompetitionRoundMapper;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.service.CompetitionService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService service;

    @Operation(summary = "Create a competition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompetitionResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Competition not created",
                    content = @Content)})
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createCompetition(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Competition to create", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateCompetitionRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Competition",
                                      "country": "GB",
                                      "startDate": "2024-01-01",
                                      "endDate": "2024-10-01",
                                      "participantIds": [
                                        10000, 10001
                                      ],
                                      "fillToParticipants": 6,
                                      "simulationValues": {
                                        "variance": 0.1,
                                        "scoreDifferenceDrawTrigger": 0.2,
                                        "drawTriggerChance": 0.3
                                      }
                                    }
                                    """)))
            @Valid @RequestBody CreateCompetitionRequestDTO dto,
            HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.generateAndSave(dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(success)));
    }

    @Operation(summary = "Find a competition by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Competition.class))}),
            @ApiResponse(responseCode = "400", description = "Competition not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompetition(@PathVariable long id,
                                                 HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return service.findCompetitionById(id).fold(
                error -> ResponseEntity.badRequest().body(error),
                competition ->
                        ResponseEntity.ok(CompetitionMapper.INSTANCE.competitionToCompetitionResponseDTO(competition)));
    }

    @Operation(summary = "Update a competition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Competition.class))}),
            @ApiResponse(responseCode = "400", description = "Competition not updated",
                    content = @Content)})
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCompetition(@PathVariable long id,
                                                    @Valid @RequestBody UpdateCompetitionRequestDTO dto,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return service.update(id, dto).fold(
                error -> ResponseEntity.badRequest().body(error),
                ResponseEntity::ok);
    }

    @Operation(summary = "Delete a competition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition deleted",
                    content = @Content),
            @ApiResponse(responseCode = "204", description = "Competition not found for deletion",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCompetition(@PathVariable long id,
                                                    HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var comp = service.findCompetitionById(id);
        if (comp.isLeft()) return ResponseEntity.noContent().build();
        service.delete(id);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Search for competitions")
    @ApiResponse(responseCode = "200", description = "Competitions according to criteria returned",
            content = @Content)
    @GetMapping("/search")
    public ResponseEntity<Page<CompetitionResponseDTO>> getAllCompetitions(HttpServletRequest request,
                                                                           SearchCompetitionCriteriaDTO criteria,
                                                                           Pageable pageable) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var result = service.search(criteria, pageable);

        return ResponseEntity.ok(new PageImpl<>(result.getContent()
                .stream()
                .map(CompetitionMapper.INSTANCE::competitionToCompetitionResponseDTO)
                .toList(),
                pageable,
                result.getTotalElements()));
    }


    @Operation(summary = "Simulate a competition with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competition simulated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(oneOf = {
                                    CompetitionSimulationSimpleResponseDTO.class,
                                    CompetitionSimulationResponseDTO.class}))}),
            @ApiResponse(responseCode = "400", description = "Competition not found",
                    content = @Content)})
    @PostMapping("/{id}/simulate")
    public ResponseEntity<Object> simulate(@PathVariable long id,
                                           @Parameter(description = "Amount of rounds to be simulated\n" +
                                                   "If value greater than rounds that can be simulated (and the league can still be simulated), " +
                                                   "simulates until the end", example = "1")
                                           @RequestParam @Min(1) Integer times,
                                           @Parameter(description = "Simplify the result (roundIDs instead of round objects)")
                                           @RequestParam(required = false, defaultValue = "false") boolean simplify,
                                           HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = service.findCompetitionById(id);
        if (findResult.isLeft()) return ResponseEntity.badRequest().body(findResult.getLeft());

        var competition = findResult.get();
        return service.simulate(findResult.get(), times).fold(
                error -> ResponseEntity.badRequest().body(error),
                success -> simplify ?
                        ResponseEntity.ok(new CompetitionSimulationSimpleResponseDTO(competition.getId(),
                                competition.getLastSimulatedRound(),
                                success.stream().map(CompetitionRound::getId).toList())) :
                        ResponseEntity.ok(new CompetitionSimulationResponseDTO(competition.getId(),
                                competition.getLastSimulatedRound(),
                                CompetitionRoundMapper.INSTANCE.roundsToResponseDTOs(success)))
        );
    }

}
