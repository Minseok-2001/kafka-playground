package minseok.kafkaplayground.notification.application.command

import java.time.Instant

data class CreateNotificationCommand(
    val memberId: Long,
    val channel: String,
    val subject: String,
    val body: String,
    val scheduledAt: Instant?,
)
