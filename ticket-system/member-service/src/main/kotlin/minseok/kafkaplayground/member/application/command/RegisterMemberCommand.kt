package minseok.kafkaplayground.member.application.command

data class RegisterMemberCommand(
    val email: String,
    val nickname: String,
    val notificationChannel: String?,
)
