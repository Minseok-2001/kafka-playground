package minseok.kafkaplayground.member.adapter.response

import minseok.kafkaplayground.member.domain.MemberAccount

fun MemberAccount.toResponse(): MemberResponse = MemberResponse(
    id = id,
    email = email,
    nickname = nickname,
    status = status.name,
    notificationChannel = notificationChannel,
)
