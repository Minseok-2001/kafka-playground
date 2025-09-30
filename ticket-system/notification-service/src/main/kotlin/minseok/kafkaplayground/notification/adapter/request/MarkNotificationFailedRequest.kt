package minseok.kafkaplayground.notification.adapter.request

import jakarta.validation.constraints.NotBlank

class MarkNotificationFailedRequest(
    @field:NotBlank
    val reason: String,
)
