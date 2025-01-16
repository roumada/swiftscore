package com.roumada.swiftscore.controller;

import com.roumada.swiftscore.model.dto.ErrorResponse;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.LoggingMessageTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/footballclub")
@AllArgsConstructor
public class FootballClubController {

    private final FootballClubDataLayer dataLayer;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFootballClub(@PathVariable long id,
                                                  HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        var findResult = dataLayer.findById(id);
        return findResult.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Couldn't find football club with ID [%s]".formatted(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllClubs(HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpoint(request));
        return ResponseEntity.ok(dataLayer.findAll());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createFootballClub(@Valid @RequestBody FootballClubDTO dto,
                                                     HttpServletRequest request) {
        log.info(LoggingMessageTemplates.getForEndpointWithBody(request, dto));
        return ResponseEntity.ok(dataLayer.saveFromDto(dto));
    }
}
