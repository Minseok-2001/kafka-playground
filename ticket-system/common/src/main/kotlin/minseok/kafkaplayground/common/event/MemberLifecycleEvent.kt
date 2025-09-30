package minseok.kafkaplayground.common.event

import java.time.Instant

data class MemberLifecycleEvent(
    val memberId: Long,
    val type: String,
    val occurredAt: Instant,
)
