package minseok.kafkaplayground.member.application

import minseok.kafkaplayground.member.application.command.ChangeMemberStatusCommand
import minseok.kafkaplayground.member.application.command.RegisterMemberCommand
import minseok.kafkaplayground.member.application.command.UpdateMemberProfileCommand
import minseok.kafkaplayground.member.domain.MemberAccount
import minseok.kafkaplayground.member.domain.MemberAccountRepository
import minseok.kafkaplayground.member.domain.MemberStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberAccountRepository: MemberAccountRepository,
    private val memberEventPublisher: MemberEventPublisher,
) {
    @Transactional
    fun register(command: RegisterMemberCommand): MemberAccount {
        memberAccountRepository.findByEmail(command.email).ifPresent {
            throw IllegalArgumentException("email already in use: ${command.email}")
        }

        val member =
            MemberAccount(
                email = command.email,
                nickname = command.nickname,
                notificationChannel = command.notificationChannel,
            )
        val saved = memberAccountRepository.save(member)
        memberEventPublisher.publish(saved.id, type = "REGISTERED")
        return saved
    }

    @Transactional
    fun updateProfile(command: UpdateMemberProfileCommand): MemberAccount {
        val member = loadMember(command.memberId)
        member.updateProfile(command.nickname, command.notificationChannel)
        val updated = memberAccountRepository.save(member)
        memberEventPublisher.publish(updated.id, type = "PROFILE_UPDATED")
        return updated
    }

    @Transactional
    fun changeStatus(command: ChangeMemberStatusCommand): MemberAccount {
        val member = loadMember(command.memberId)
        when (command.targetStatus) {
            MemberStatus.ACTIVE -> member.activate()
            MemberStatus.SUSPENDED -> member.suspend()
            MemberStatus.WITHDRAWN -> member.withdraw()
        }
        val updated = memberAccountRepository.save(member)
        memberEventPublisher.publish(
            updated.id,
            type = "STATUS_${updated.status.name}",
        )
        return updated
    }

    @Transactional(readOnly = true)
    fun find(memberId: Long): MemberAccount = loadMember(memberId)

    private fun loadMember(memberId: Long): MemberAccount = memberAccountRepository
        .findById(memberId)
        .orElseThrow { IllegalArgumentException("member not found: $memberId") }
}
