# Scalable Order Processing Platform
(Java + Spring Boot + Azure Event Hubs + Avro + Gradle)

A distributed, event-driven order processing system built with Java Spring Boot microservices and Azure Event Hubs.

This project demonstrates backend architecture patterns aligned with mid-level software engineering roles at large-scale cloud companies.

---

# Overview

This platform simulates an e-commerce order workflow using asynchronous event-driven communication.

High-level flow:

1. Client sends raw JSON to the Order API
2. Order service processes the request
3. Order service wraps the request into an Avro event
4. Event is published to Azure Event Hubs
5. Downstream services (Payment, Inventory) consume the event and emit their own events

All services communicate exclusively through events.

There are no direct service-to-service HTTP calls.

---

# Architecture Principles

- Event-driven microservices
- Single Schema Group strategy in Azure Schema Registry (`sopp-dev`)
- Avro-based event contracts
- Gradle multi-module structure
- Domain-based service separation
- Loose coupling between services
- Clear separation between HTTP handling and event publishing

---


# Service Structure Convention

Each service contains:

- `controller` — REST endpoints that accept raw JSON
- `service` — business logic and Event Hub publishing
- `repository` — persistence layer (optional early on)
- `util` — correlation ID utilities, JSON helpers, etc.

There are no dedicated DTO or mapper folders.

Raw JSON is accepted and wrapped into a general Avro event.

---

# Avro Event Strategy

This project uses a single general Avro schema:

`schemas/avro/PlatformEvent.avsc`

Instead of defining one schema per event type, the system uses a generic event envelope.

Example structure:

```json
{
  "type": "record",
  "name": "PlatformEvent",
  "namespace": "com.sopp.event",
  "fields": [
    { "name": "eventId", "type": "string" },
    { "name": "eventType", "type": "string" },
    { "name": "correlationId", "type": "string" },
    { "name": "occurredAt", "type": "string" },
    { "name": "source", "type": "string" },
    { "name": "payload", "type": "string" }
  ]
}
```

The payload field contains serialized domain JSON.

All schemas are registered under a single Schema Group in Azure:

`sopp-dev`

Generated Avro classes are build artifacts and are not committed to the repository.

---

# Example API Flow

## POST /orders/raw

Client sends raw JSON:

```json
{
  "customerId": "c123",
  "items": [
    { "sku": "A1", "quantity": 2, "unitPrice": 12.99 }
  ]
}
```

---

# Order Service Processing Steps
1. Accept raw JSON request.
2. Generate a unique orderId.
3. Generate eventId and correlationId.
4. Wrap the request payload into a PlatformEvent (Avro).
5. Set:
    - eventType = "OrderCreated"
    - source = "order-service"
    - occurredAt = ISO-8601 timestamp
6. Serialize Avro event.
7. Publish event to Azure Event Hubs.

Example PlatformEvent Payload:

```json
{
  "eventId": "e-789",
  "eventType": "OrderCreated",
  "correlationId": "corr-456",
  "occurredAt": "2025-01-01T12:00:00Z",
  "source": "order-service",
  "payload": "{\"customerId\":\"c123\",\"items\":[{\"sku\":\"A1\",\"quantity\":2,\"unitPrice\":12.99}]}"
}
```

Expected Response:
```json
{
  "orderId": "o-123",
  "status": "PENDING"
}
```

--- 

# Event Flow (High Level)
Client
↓
Order Service
↓
Azure Event Hubs
↓
Payment Service
↓
Inventory Service

Each downstream service:
- Consumes PlatformEvent
- Checks eventType
- Processes accordingly
- Emits a new PlatformEvent with its own eventType

Examples:
- PaymentAuthorized
- PaymentFailed
- InventoryReserved
- InventoryFailed

---

# Build Instructions
From project root: `./gradlew clean build`

Run individual service: `./gradlew :services:order:bootRun`

## Local Development Notes
- Azure Event Hub connection string must be set via environment variables.
- Do not commit secrets.
- Use .env locally if needed.
- Generated Avro classes are build artifacts and should not be committed.

## .gitignore Essentials
```gitignore
.gradle/
**/build/
.idea/
*.iml
.DS_Store
.env
```

---

# Roadmap
- Add Payment service consumer
- Add Inventory service consumer
- Implement Outbox pattern
- Add idempotent consumers
- Introduce schema versioning strategy
- Add Terraform-managed Azure infrastructure
- Add observability (metrics + tracing)

---

# Purpose

This project demonstrates:
- event-driven architecture
- Cloud messaging patterns
- Schema Registry usage
- Gradle multi-module backend structure
- Clean domain-oriented service design

Designed to reflect real-world backend systems at large-scale cloud companies.

---