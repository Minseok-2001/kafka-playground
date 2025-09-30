package minseok.kafkaplayground.member.adapter.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class RegisterMemberRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val nickname: String,
    val notificationChannel: String?,
)
