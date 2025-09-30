package minseok.kafkaplayground.notification.adapter.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

class CreateNotificationRequest(
    @field:NotNull
    val memberId: Long,
    @field:NotBlank
    val channel: String,
    @field:NotBlank
    val subject: String,
    @field:NotBlank
    val body: String,
    val scheduledAt: Instant?,
)
