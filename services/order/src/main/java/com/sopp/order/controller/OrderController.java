package com.sopp.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sopp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/raw")
    public ResponseEntity<Map<String, Object>> createRaw(@RequestBody JsonNode rawJson) {
        String orderId = orderService.processRawOrder(rawJson);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", "PENDING"
        ));
    }
}