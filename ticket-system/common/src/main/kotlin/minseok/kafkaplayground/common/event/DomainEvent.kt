package minseok.kafkaplayground.common.event

import java.time.Instant
import java.util.UUID

abstract class DomainEvent(
    val id: UUID = UUID.randomUUID(),
    val occurredAt: Instant = Instant.now(),
)
