package com.roumada.swiftscore.model.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {
}
