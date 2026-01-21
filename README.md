# FinOrder Platform - Financial order platform

FinOrder is a small order and payment system built as event-driven microservices. It has an order service, an inventory service, and a payment ledger service that talks with Kafka events, and it uses MongoDB for storage. It is meant to show real-world patterns in a simple way. 

## Why this project stands out
- **Event-driven microservices with Kafka + DLQ**
- **Idempotent APIs + at-least-once consumers**
- **Ledger-style immutable postings**
- **Prometheus/Grafana dashboards included**
- **One-command local run via Docker Compose**
- **Postman collection demonstrates 3 end-to-end flows**

## Features
1. **Order creation and confirmation**
    - **How to see it:** Create an order, then confirm it to publish an order event.
    - **Endpoints:** `POST /orders`, `POST /orders/{id}/confirm`

2. **Inventory reservation with failure handling**
    - **How to see it:** Create and confirm an order, then check the order status for reservation outcomes.
    - **Endpoints:** `GET /orders/{id}`
      
3. **Event-driven microservices with Kafka + DLQ**
   - Run the Postman flows and watch each service log Kafka events.
   - For a failure path, run `flow2_inventory_reservation_failure.json` and check the inventory service logs for DLQ publishing.

4. **Payment intents, capture, and refund**
    - **How to see it:** Create a payment intent, capture it, then refund it.
    - **Endpoints:** `POST /payment-intents`, `POST /payment-intents/{id}/capture`, `POST /payment-intents/{id}/refund`

5. **Ledger-style immutable postings**
    - **How to see it:** Capture and refund a payment, then inspect ledger entries for the order.
    - **Endpoint:** `GET /ledger-entries?orderId={orderId}`

6. **Idempotent APIs**
    - **How to see it:** Send the same create order or create payment intent request with the same idempotency key.
    - **Headers:** `Idempotency-Key`

7. **Operational observability**
    - **How to see it:**
        - Prometheus metrics: `GET /actuator/prometheus`
        - Service health: `GET /actuator/health`

8. **Prometheus/Grafana dashboards**
    - **How to see it:** Open `http://localhost:9090` for Prometheus and `http://localhost:3000` for Grafana (admin/admin).

## Architecture overview
```
order-service/        → Order API + order events
inventory-service/    → Inventory consumer + inventory events + DLQ
pay-ledger-service/   → Payment API + ledger postings
infra/                → Kafka, MongoDB, Prometheus, Grafana (Docker Compose)
postman/              → 3 end-to-end Postman flows
```

## Key engineering decisions
- **Idempotency keys on APIs**: safe retries for create order and create payment intent.
- **At-least-once event handling**: consumers can retry, and DLQ catches bad messages.
- **Ledger-style postings**: every money move is written as new debit/credit entries.

## Tech stack
- Java 17
- Spring Boot 3.x
- Kafka + Zookeeper
- MongoDB
- Prometheus + Grafana

## Local development

### Prerequisites
- Java 17+
- Docker

### 1) Start core dependencies
```bash
docker compose -f infra/docker-compose.yml up -d
```

### 2) Start the services (each in its own terminal)
```bash
cd order-service && ./mvnw spring-boot:run
cd inventory-service && ./mvnw spring-boot:run
cd pay-ledger-service && ./mvnw spring-boot:run
```

Services run on:
- Order API: `http://localhost:8081`
- Inventory API: `http://localhost:8082`
- Payment ledger API: `http://localhost:8083`

## Observability
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)

## Postman flows
Import the JSON files in `postman/` to try these end-to-end flows:
- `flow1_happy_path.json`
- `flow2_inventory_reservation_failure.json`
- `flow3_payment_refund.json`

## Future development checklist
- [ ] Add OpenAPI/Swagger docs for each service.
- [ ] Add contract tests for Kafka events.
- [ ] Add unit and integration test coverage reports.
- [ ] Add retries and backoff tuning per consumer.
- [ ] Add alerting rules for DLQ growth and consumer lag.

## Notes
- Kafka topics used: `order-events`, `inventory-events`, `payment-events`.
- Inventory consumers publish to a DLQ on errors.
- The Postman flows show happy path, inventory failure, and payment refund.
