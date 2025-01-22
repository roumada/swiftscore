package com.roumada.swiftscore.logic;

import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CompetitionDatesProvider {
    @Getter
    LocalDate start;
    long offset = 0;
    @Getter
    long step;

    public CompetitionDatesProvider(LocalDate start, LocalDate end, int clubsAmount) {
        this.start = start;
        step = Math.max(1, (ChronoUnit.DAYS.between(start, end) / clubsAmount));
    }

    public LocalDate next() {
        var nextDate = start.plusDays(offset);
        offset += step;
        return nextDate;
    }
}
