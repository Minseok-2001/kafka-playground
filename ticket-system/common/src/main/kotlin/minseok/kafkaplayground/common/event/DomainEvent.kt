package minseok.kafkaplayground.common.event

import java.time.Instant
import minseok.kafkaplayground.common.support.TsidFactoryProvider

abstract class DomainEvent(
    val id: Long = TsidFactoryProvider.nextLong(),
    val occurredAt: Instant = Instant.now(),
)
