package com.roumada.swiftscore.unit.logic.match;

import com.roumada.swiftscore.logic.match.HorusSeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HorusSeriesTests {

    @ParameterizedTest
    @CsvSource({
            "0, 0.49, 1",
            "0, 0.5, 1",
            "0, 0.51, 1",
            "1, 0.49, 1",
            "1, 0.5, 1",
            "1, 0.51, 2",
            "1, 1, 2",
            "2, 0.49, 1",
            "2, 0.5, 1",
            "2, 0.51, 2",
            "2, 0.74, 2",
            "2, 0.75, 2",
            "2, 0.78, 3",
            "2, 1, 3",
            "3, 0, 1",
            "3, 0.49, 1",
            "3, 0.5, 1",
            "3, 0.51, 2",
            "3, 0.74, 2",
            "3, 0.75, 2",
            "3, 0.78, 3",
            "3, 0.874, 3",
            "3, 0.876, 4",
            "3, 1, 4",
    })
    @DisplayName("Should return correct amount of goals")
    void shouldReturnCorrectGoalsAmount(int ceil, double chance, int expected) {
        assertEquals(expected, HorusSeries.getFromOne(ceil, chance));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0.49, 0",
            "0, 0.5, 0",
            "0, 0.51, 0",
            "1, 0.49, 0",
            "1, 0.5, 0",
            "1, 0.51, 1",
            "1, 1, 1",
            "2, 0.49, 0",
            "2, 0.5, 0",
            "2, 0.51, 1",
            "2, 0.74, 1",
            "2, 0.75, 1",
            "2, 0.78, 2",
            "2, 1, 2",
            "3, 0, 0",
            "3, 0.49, 0",
            "3, 0.5, 0",
            "3, 0.51, 1",
            "3, 0.74, 1",
            "3, 0.75, 1",
            "3, 0.78, 2",
            "3, 0.874, 2",
            "3, 0.876, 3",
            "3, 1, 3",
    })
    @DisplayName("Should return correct amount of goals for a draw")
    void shouldReturnCorrectGoalsAmountForDraw(int ceil, double chance, int expected) {
        assertEquals(expected, HorusSeries.getFromZero(ceil, chance));
    }

}
