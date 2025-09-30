package minseok.kafkaplayground.notification.application.command

data class MarkNotificationSentCommand(
    val notificationId: Long,
)
