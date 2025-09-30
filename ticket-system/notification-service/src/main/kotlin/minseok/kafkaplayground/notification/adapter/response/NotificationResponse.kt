package minseok.kafkaplayground.notification.adapter.response

import java.time.Instant

data class NotificationResponse(
    val id: Long,
    val memberId: Long,
    val channel: String,
    val subject: String,
    val body: String,
    val status: String,
    val scheduledAt: Instant?,
    val sentAt: Instant?,
    val failureReason: String?,
)
