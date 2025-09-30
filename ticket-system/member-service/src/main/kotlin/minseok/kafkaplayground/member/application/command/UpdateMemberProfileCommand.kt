package minseok.kafkaplayground.member.application.command

data class UpdateMemberProfileCommand(
    val memberId: Long,
    val nickname: String,
    val notificationChannel: String?,
)
