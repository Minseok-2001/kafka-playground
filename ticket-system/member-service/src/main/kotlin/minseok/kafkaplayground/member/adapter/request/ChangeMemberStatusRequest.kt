package minseok.kafkaplayground.member.adapter.request

import jakarta.validation.constraints.NotBlank

class ChangeMemberStatusRequest(
    @field:NotBlank
    val status: String,
)
