package com.roumada.swiftscore.util;

import com.roumada.swiftscore.model.FootballClub;

import java.util.List;

public class PersistenceTestUtils {
    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs){
        return getIdsOfSavedClubs(clubs, clubs.size());
    }

    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs, int amount){
        return clubs.stream().map(FootballClub::getId).limit(amount).toList();
    }
}
