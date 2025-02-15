package com.roumada.swiftscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.request.CreateCompetitionRequestDTO;
import com.roumada.swiftscore.model.dto.request.CreateFootballClubRequestDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final CompetitionService competitionService;
    private final FootballClubDataLayer fcDataLayer;
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:data/footballclubs.json")
    private Resource footballClubsResource;

    @Value("classpath:data/competitions.json")
    private Resource competitionsResource;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data loader initialized");
        dropPrevious();
        saveFCs();
    }

    private void dropPrevious() {
        mongoTemplate.getDb().drop();
        log.info("Previous database dropped");
    }

    private void saveFCs() {
        var clubs = loadFootballClubs();
        if (!clubs.isEmpty()) {
            fcDataLayer.saveAll(clubs);
            log.info("Football clubs saved");
        }
        var competitionRequests = loadCompetitionRequests();
        for (CreateCompetitionRequestDTO request : competitionRequests) {
            competitionService.generateAndSave(request);
            log.info("Competitions saved");
        }

    }

    public List<FootballClub> loadFootballClubs() {
        Map<CreateFootballClubRequestDTO, Boolean> validClubDTOs = new HashMap<>();
        try {
            List<CreateFootballClubRequestDTO> clubDTOs = objectMapper.readValue(footballClubsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CreateFootballClubRequestDTO.class));
            validClubDTOs = clubDTOs.stream()
                    .collect(Collectors.toMap(club -> club, club -> false));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
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
            log.error(e.getLocalizedMessage());
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
