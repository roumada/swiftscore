package com.roumada.swiftscore.logging;

import java.util.IllegalFormatException;

public enum Messages {

    FORMAT_FAILURE("Invalid arguments provided."),

    FOOTBALL_CLUBS_COULDNT_RETRIEVE_ALL_FROM_IDS("Couldn't retrieve all clubs for given IDs and country."),
    FOOTBALL_CLUBS_NOT_ENOUGH_CLUBS_FROM_COUNTRY("Couldn't find enough clubs from given country to fill in the league."),
    FOOTBALL_CLUBS_PARTICIPANTS_AMT_LOWER_THAN_FCIDS(
            "Participants parameter [%s] lower than amount of club IDs provided ([%s]). Returning clubs with denoted club IDs only."),

    COMPETITION_SIMULATED("Competition with id [%s] simulated."),
    COMPETITION_SIMULATED_UNTIL_END("Attempting to simulate a competition [%s] times while the competition has only [%s] rounds left. Simulating the entire competition."),
    COMPETITION_NOT_FOUND("Competition with ID [%s] not found."),
    COMPETITION_INVALID_RELEGATION_SPOTS_AMOUNT(
            "New relegation spots amount [%s] exceeds amount of participants in competition [%s] by at least two."),
    COMPETITION_CANNOT_SIMULATE("Cannot simulate competition [%s] further."),
    COMPETITION_CANNOT_GENERATE_CLUBS_MUST_BE_EVEN("Failed to generate competition - the amount of clubs participating must be even.");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        String result;
        try {
            result = String.format(message, args);
        } catch (IllegalFormatException e) {
            return FORMAT_FAILURE.message;
        }
        return result;
    }
}
