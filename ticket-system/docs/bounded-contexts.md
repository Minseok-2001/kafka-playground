# Bounded Context Map

| Context                         | Responsibility                                                        | Primary Data Stores                          | External Contracts                                       |
|---------------------------------|-----------------------------------------------------------------------|----------------------------------------------|----------------------------------------------------------|
| Gateway                         | Auth, rate limiting, routing, SSE/WebSocket fan-out                   | Stateless (Redis for tokens in future)       | HTTP → other services, WebSocket/SSE to frontend         |
| Member                          | Account profile, auth credentials, preferences, notification channels | MySQL member schema                          | Publishes member.created, offers REST for profile lookup |
| Ticketing                       | Event schedule, seat inventory, reservation lifecycle                 | MySQL 	icket schema                          | Consumes queue tokens, emits 	icket.reservation events   |
| WaitingRoom                     | Queue tokens, throttling policies, admission scheduling               | Redis (token TTL), MySQL waiting for history | REST for token issuance, Kafka waiting.admitted          |
| Payment                         | Payment intent, mock PG approval, refund, compensation                | MySQL payment schema                         | Publishes payment.transaction, consumes coupon.applied   |
| Promotion                       | Coupon policy, issuance, redemption, benefit calc                     | MySQL promotion schema                       | Emits coupon.applied, REST for eligibility               |
| Notification                    | Email/Web push/SMS, template + delivery state                         | MySQL                                        
 otification, external SMTP/Push | Consumes events, exposes REST for testing                             |

## Interactions

- **Ticketing ⇄ WaitingRoom**: Ticketing verifies queue tokens before seat reservation. WaitingRoom
  sends waiting.admitted events consumed by frontend notification via Gateway/Notification.
- **Ticketing ⇄ Payment**: Ticketing publishes reservation events; Payment follows up with
  transaction events. Saga handled via Outbox (future).
- **Payment ⇄ Promotion**: Payment queries coupon service to compute discount and records applied
  coupon id; Promotion emits redemption events.
- **Notification**: Subscribes to reservation/payment/waiting events to notify users; can be
  triggered via REST for manual tests.
- **Member**: Source of truth for member profile + notification endpoints. Other services reference
  memberId only and look up via Member REST when needed or cache locally.

## Deployment Notes

- Each context packaged as independent Spring Boot app under separate Gradle module with dedicated
  Dockerfile and compose overlay.
- Shared message schemas remain in common module. New topics:
    - waiting.admitted.events
    - coupon.lifecycle.events
    -

otification.dispatch.events

- Credentials/secrets configured through environment variables in compose overlays. Each MySQL
  instance uses isolated volume.

## Next Steps

1. Scaffold new modules (member-service, promotion-service, waiting-room-service,
   otification-service).
2. Define DB migrations + repositories with TSID ids and TDD-first aggregates.
3. Wire Kafka topics + compose overlays, ensuring health endpoints exposed for gateway + monitoring.
4. Extend docs with API contracts once controllers stabilise.
