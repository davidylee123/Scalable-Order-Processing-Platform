package com.sopp.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventPublisherService {

    public void publish(String eventJson) {
        // TODO: replace with Azure Event Hubs producer
        log.info("PUBLISH -> {}", eventJson);
    }
}