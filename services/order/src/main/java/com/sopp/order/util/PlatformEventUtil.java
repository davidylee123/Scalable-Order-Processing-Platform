package com.sopp.order.util;

import java.time.Instant;
import java.util.UUID;

public final class PlatformEventUtil {

    private PlatformEventUtil() {}

    public static String buildPlatformEvent(String eventType, String source, String payloadJson) {
        String eventId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        String occurredAt = Instant.now().toString();

        return """
            {
              "eventId": "%s",
              "eventType": "%s",
              "correlationId": "%s",
              "occurredAt": "%s",
              "source": "%s",
              "payload": %s
            }
            """.formatted(eventId, eventType, correlationId, occurredAt, source, payloadJson);
    }
}