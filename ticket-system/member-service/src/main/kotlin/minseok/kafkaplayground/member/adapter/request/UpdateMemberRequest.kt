package minseok.kafkaplayground.member.adapter.request

import jakarta.validation.constraints.NotBlank

class UpdateMemberRequest(
    @field:NotBlank
    val nickname: String,
    val notificationChannel: String?,
)
