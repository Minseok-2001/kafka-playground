package minseok.kafkaplayground.common.event

import minseok.kafkaplayground.common.support.TsidFactoryProvider
import java.time.Instant

abstract class DomainEvent(
    val id: Long = TsidFactoryProvider.nextLong(),
    val occurredAt: Instant = Instant.now(),
)
