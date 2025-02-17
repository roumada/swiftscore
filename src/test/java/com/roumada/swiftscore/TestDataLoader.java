package com.roumada.swiftscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.service.CompetitionService;
import groovy.util.logging.Slf4j;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("test")
public class TestDataLoader {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private FootballClubRepository fcRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private Validator validator;
    private List<FootballClub> cachedClubs;
    private List<Competition> cachedCompetitions;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:data/footballclubs.json")
    private Resource footballClubsResource;

    @Value("classpath:data/competitions.json")
    private Resource competitionsResource;

    public void saveFCs() {
        if (cachedClubs != null) {
            fcRepository.saveAll(cachedClubs);
        } else {
            cachedClubs = loadFootballClubs();
            fcRepository.saveAll(cachedClubs);
        }
    }

    public void saveCompetitions() {
        if (cachedCompetitions != null) {
            competitionRepository.saveAll(cachedCompetitions);
        } else {
            List<Competition> comps = new ArrayList<>();
            var competitionRequests = loadCompetitionRequests();
            for (CreateCompetitionRequestDTO request : competitionRequests) {
                comps.add(competitionService.generateAndSave(request).get());
            }
            cachedCompetitions = comps;
        }
    }

    private List<FootballClub> loadFootballClubs() {
        Map<CreateFootballClubRequestDTO, Boolean> validClubDTOs = new HashMap<>();
        try {
            List<CreateFootballClubRequestDTO> clubDTOs = objectMapper.readValue(footballClubsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CreateFootballClubRequestDTO.class));
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

    private List<CreateCompetitionRequestDTO> loadCompetitionRequests() {
        Map<CreateCompetitionRequestDTO, Boolean> validCompetitionDTOs = new HashMap<>();
        try {
            List<CreateCompetitionRequestDTO> competitionDTOs = objectMapper.readValue(competitionsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CreateCompetitionRequestDTO.class));
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
