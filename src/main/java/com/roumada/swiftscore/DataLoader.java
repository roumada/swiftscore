package com.roumada.swiftscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.dto.FootballClubDTO;
import com.roumada.swiftscore.model.mapper.FootballClubMapper;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final FootballClubRepository footballClubRepository;

    @Value("classpath:data/footballclubs.json")
    private Resource footballClubsResource;

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
        footballClubRepository.saveAll(loadFootballClubs());
        log.info("Football clubs saved");
    }

    public List<FootballClub> loadFootballClubs() {
        List<FootballClubDTO> clubDTOs;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            clubDTOs = objectMapper.readValue(footballClubsResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FootballClubDTO.class));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        return clubDTOs.stream().map(FootballClubMapper.INSTANCE::footballClubDTOtoFootballClub).toList();
    }
}
