package com.roumada.swiftscore.model;

import java.util.List;

public record ErrorResponse(List<String> requestErrors) {
}
