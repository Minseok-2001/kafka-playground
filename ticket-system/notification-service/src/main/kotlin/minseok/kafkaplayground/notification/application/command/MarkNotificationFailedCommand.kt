package minseok.kafkaplayground.notification.application.command

data class MarkNotificationFailedCommand(
    val notificationId: Long,
    val reason: String,
)
