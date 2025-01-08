package com.roumada.swiftscore.util;

import com.roumada.swiftscore.model.FootballClub;

import java.util.List;

public class PersistenceTestUtils {
    public static List<Long> getIdsOfSavedClubs(List<FootballClub> clubs){
        return clubs.stream().map(FootballClub::getId).toList();
    }
}
