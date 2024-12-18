package com.roumada.swiftscore;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final FootballClubRepository footballClubRepository;

    @Override
    public void run(String... args) throws Exception {
        dropPrevious();
        saveFCs();
    }

    private void dropPrevious() {
        mongoTemplate.getDb().drop();
        log.info("Previous database dropped");
    }

    private void saveFCs() {
        footballClubRepository.saveAll(List.of(
                FootballClub.builder().name("FC1").victoryChance(0.1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.2).build(),
                FootballClub.builder().name("FC3").victoryChance(0.3).build(),
                FootballClub.builder().name("FC4").victoryChance(0.4).build(),
                FootballClub.builder().name("FC5").victoryChance(0.5).build(),
                FootballClub.builder().name("FC6").victoryChance(0.6).build(),
                FootballClub.builder().name("FC7").victoryChance(0.7).build(),
                FootballClub.builder().name("FC8").victoryChance(0.8).build(),
                FootballClub.builder().name("FC9").victoryChance(0.9).build(),
                FootballClub.builder().name("FC10").victoryChance(1).build()
        ));
    }
}
