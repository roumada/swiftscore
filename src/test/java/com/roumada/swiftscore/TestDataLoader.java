package com.roumada.swiftscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequest;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequest;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.service.CompetitionService;
import groovy.util.logging.Slf4j;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("test")
public class TestDataLoader {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private FootballClubRepository fcRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private Validator validator;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:data/footballclubs.json")
    private Resource footballClubsResource;

    @Value("classpath:data/competitions.json")
    private Resource competitionsResource;

    public void saveFCs() {
        fcRepository.saveAll(loadFootballClubs());
    }

    public Competition saveCompetition() {
        var compRequest = loadCompetitionRequests().get(0);
        return competitionService.generateAndSave(compRequest).get();
    }

    public void saveCompetitions() {
        var competitionRequests = loadCompetitionRequests();
        for (CreateCompetitionRequest request : competitionRequests) {
            competitionService.generateAndSave(request);
        }
    }

    private List<FootballClub> loadFootballClubs() {
        Map<CreateFootballClubRequest, Boolean> validClubDTOs = new HashMap<>();
        try {
            List<CreateFootballClubRequest> clubDTOs = objectMapper.readValue(footballClubsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CreateFootballClubRequest.class));
            validClubDTOs = clubDTOs.stream()
                    .collect(Collectors.toMap(club -> club, club -> false));
        } catch (IOException e) {
        }

        if (validClubDTOs.isEmpty()) return Collections.emptyList();

        return validClubDTOs.entrySet()
                .stream()
                .map(kv -> {
                    if (validator.validate(kv.getKey()).isEmpty()) kv.setValue(true);
                    return kv;
                })
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .map(FootballClubMapper.INSTANCE::requestToObject)
                .toList();
    }

    private List<CreateCompetitionRequest> loadCompetitionRequests() {
        Map<CreateCompetitionRequest, Boolean> validCompetitionDTOs = new HashMap<>();
        try {
            List<CreateCompetitionRequest> competitionDTOs = objectMapper.readValue(competitionsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CreateCompetitionRequest.class));
            validCompetitionDTOs = competitionDTOs.stream()
                    .collect(Collectors.toMap(comp -> comp, comp -> false));
        } catch (IOException e) {
        }

        if (validCompetitionDTOs.isEmpty()) return Collections.emptyList();

        return validCompetitionDTOs.entrySet()
                .stream()
                .map(kv -> {
                    if (validator.validate(kv.getKey()).isEmpty()) kv.setValue(true);
                    return kv;
                })
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }
}
