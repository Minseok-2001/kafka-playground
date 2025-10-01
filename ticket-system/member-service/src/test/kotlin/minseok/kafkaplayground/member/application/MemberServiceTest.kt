package minseok.kafkaplayground.member.application

import io.mockk.Called
import io.mockk.MockKMatcherScope
import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import minseok.kafkaplayground.member.application.command.ChangeMemberStatusCommand
import minseok.kafkaplayground.member.application.command.RegisterMemberCommand
import minseok.kafkaplayground.member.application.command.UpdateMemberProfileCommand
import minseok.kafkaplayground.member.domain.MemberAccount
import minseok.kafkaplayground.member.domain.MemberAccountRepository
import minseok.kafkaplayground.member.domain.MemberStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemberServiceTest {
    private val memberAccountRepository = mockk<MemberAccountRepository>()
    private val memberEventPublisher = mockk<MemberEventPublisher>(relaxed = true)
    private lateinit var memberService: MemberService

    @BeforeEach
    fun setUp() {
        memberService = MemberService(memberAccountRepository, memberEventPublisher)
    }

    @Test
    fun `should register new member and publish lifecycle event`() {
        val command =
            RegisterMemberCommand(
                email = "test@ticket.com",
                nickname = "ticketfan",
                notificationChannel = "EMAIL",
            )
        val persisted =
            MemberAccount(
                email = command.email,
                nickname = command.nickname,
                notificationChannel = command.notificationChannel,
            )
        setId(persisted, 777)

        val savedMember = slot<MemberAccount>()
        given { memberAccountRepository.findByEmail(command.email) } returns Optional.empty()
        given { memberAccountRepository.save(capture(savedMember)) } returns persisted

        val result = memberService.register(command)

        assertEquals(777, result.id)
        assertEquals("ticketfan", result.nickname)
        val publishedAt = slot<Instant>()
        then { memberEventPublisher.publish(777, "REGISTERED", capture(publishedAt)) }
    }

    @Test
    fun `should reject duplicated email`() {
        val command =
            RegisterMemberCommand(
                email = "dup@ticket.com",
                nickname = "dup",
                notificationChannel = null,
            )
        val existing =
            MemberAccount(
                email = command.email,
                nickname = command.nickname,
                notificationChannel = null,
            )
        setId(existing, 1)

        given { memberAccountRepository.findByEmail(command.email) } returns Optional.of(existing)

        assertFailsWith<IllegalArgumentException> {
            memberService.register(command)
        }
        verify { memberEventPublisher wasNot Called }
    }

    @Test
    fun `should update profile and emit update event`() {
        val member =
            MemberAccount(
                email = "profile@ticket.com",
                nickname = "old",
                notificationChannel = null,
            )
        setId(member, 101)

        given { memberAccountRepository.findById(member.id) } returns Optional.of(member)
        given { memberAccountRepository.save(member) } returns member

        val result =
            memberService.updateProfile(
                UpdateMemberProfileCommand(
                    memberId = member.id,
                    nickname = "new",
                    notificationChannel = "PUSH",
                ),
            )

        assertEquals("new", result.nickname)
        assertEquals("PUSH", result.notificationChannel)
        val publishedAt = slot<Instant>()
        then { memberEventPublisher.publish(member.id, "PROFILE_UPDATED", capture(publishedAt)) }
    }

    @Test
    fun `should change status and emit lifecycle`() {
        val member =
            MemberAccount(
                email = "status@ticket.com",
                nickname = "status",
                notificationChannel = null,
            )
        setId(member, 202)

        given { memberAccountRepository.findById(member.id) } returns Optional.of(member)
        given { memberAccountRepository.save(member) } returns member

        val result =
            memberService.changeStatus(
                ChangeMemberStatusCommand(
                    memberId = member.id,
                    targetStatus = MemberStatus.SUSPENDED,
                ),
            )

        assertEquals(MemberStatus.SUSPENDED, result.status)
        val publishedAt = slot<Instant>()
        then { memberEventPublisher.publish(member.id, "STATUS_SUSPENDED", capture(publishedAt)) }
    }

    private fun setId(
        entity: MemberAccount,
        value: Long,
    ) {
        val field = entity.javaClass.superclass!!.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(entity, value)
    }
}

private fun <T> given(block: MockKMatcherScope.() -> T) = every(block)

private fun then(block: MockKVerificationScope.() -> Unit) = verify(verifyBlock = block)
