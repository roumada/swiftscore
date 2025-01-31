package com.roumada.swiftscore.util;

import jakarta.servlet.http.HttpServletRequest;

public final class LoggingMessageTemplates {

    private LoggingMessageTemplates() {
    }

    public static String getForEndpoint(HttpServletRequest request) {
        return "Request URL: %s Method: %s".formatted(
                request.getRequestURL().toString(),
                request.getMethod());
    }

    public static String getForEndpointWithBody(HttpServletRequest request, Object body) {
        return "Request URL: %s Method: %s Body: %s".formatted(
                request.getRequestURL().toString(),
                request.getMethod(),
                body.toString());
    }
}
