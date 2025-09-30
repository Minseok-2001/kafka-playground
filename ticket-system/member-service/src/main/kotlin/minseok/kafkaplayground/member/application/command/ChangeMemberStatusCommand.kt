package minseok.kafkaplayground.member.application.command

import minseok.kafkaplayground.member.domain.MemberStatus

data class ChangeMemberStatusCommand(
    val memberId: Long,
    val targetStatus: MemberStatus,
)
