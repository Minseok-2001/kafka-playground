package minseok.kafkaplayground.notification.adapter.response

import minseok.kafkaplayground.notification.domain.NotificationRequest

fun NotificationRequest.toResponse(): NotificationResponse = NotificationResponse(
    id = id,
    memberId = memberId,
    channel = channel,
    subject = subject,
    body = body,
    status = status.name,
    scheduledAt = scheduledAt,
    sentAt = sentAt,
    failureReason = failureReason,
)
