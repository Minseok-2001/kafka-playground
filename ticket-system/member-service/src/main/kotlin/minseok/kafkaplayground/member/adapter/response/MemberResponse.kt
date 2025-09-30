package minseok.kafkaplayground.member.adapter.response

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val status: String,
    val notificationChannel: String?,
)
