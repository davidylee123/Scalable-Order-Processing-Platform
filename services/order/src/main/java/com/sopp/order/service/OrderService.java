package com.sopp.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sopp.order.util.PlatformEventUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final EventPublisherService eventPublisherService;

    public String processRawOrder(JsonNode rawJson) {
        String orderId = UUID.randomUUID().toString();

        // Wrap the raw JSON into the general PlatformEvent envelope
        String eventJson = PlatformEventUtil.buildPlatformEvent(
                "OrderCreated",
                "order-service",
                rawJson.toString()
        );

        eventPublisherService.publish(eventJson);

        return orderId;
    }
}