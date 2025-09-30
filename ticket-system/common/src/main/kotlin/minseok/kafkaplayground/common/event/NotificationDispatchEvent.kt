package minseok.kafkaplayground.common.event

import java.time.Instant

data class NotificationDispatchEvent(
    val notificationId: Long,
    val memberId: Long,
    val channel: String,
    val status: String,
    val occurredAt: Instant,
)
