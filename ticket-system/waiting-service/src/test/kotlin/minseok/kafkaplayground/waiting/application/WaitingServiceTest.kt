package minseok.kafkaplayground.waiting.application

import io.mockk.Called
import io.mockk.MockKMatcherScope
import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import minseok.kafkaplayground.waiting.application.command.AdmitWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.IssueWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.WaitingStatusQuery
import minseok.kafkaplayground.waiting.domain.WaitingStatus
import minseok.kafkaplayground.waiting.domain.WaitingTicket
import minseok.kafkaplayground.waiting.domain.WaitingTicketRepository
import minseok.kafkaplayground.waiting.support.WaitingQueueStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional
import kotlin.test.assertEquals

class WaitingServiceTest {
    private val waitingTicketRepository = mockk<WaitingTicketRepository>()
    private val waitingQueueStore = mockk<WaitingQueueStore>(relaxed = true)
    private val waitingEventPublisher = mockk<WaitingEventPublisher>(relaxed = true)
    private val clock = Clock.fixed(Instant.parse("2025-10-01T00:00:00Z"), ZoneOffset.UTC)
    private lateinit var waitingService: WaitingService

    @BeforeEach
    fun setUp() {
        waitingService =
            WaitingService(
                waitingTicketRepository,
                waitingQueueStore,
                waitingEventPublisher,
                clock,
            )
    }

    @Test
    fun `should issue new ticket and enqueue`() {
        val command = IssueWaitingTicketCommand(queueCode = "concert", memberId = 10)
        val issued =
            WaitingTicket(
                queueCode = command.queueCode,
                memberId = command.memberId,
                issuedSequence = 1,
                expiredAt = Instant.now(clock).plus(Duration.ofMinutes(15)),
            )
        setId(issued, 100)

        given {
            waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
                command.queueCode,
                command.memberId,
                listOf(WaitingStatus.WAITING),
            )
        } returns Optional.empty()
        val savedTicket = slot<WaitingTicket>()
        given { waitingTicketRepository.findTopByQueueCodeOrderByIssuedSequenceDesc(command.queueCode) } returns null
        given { waitingTicketRepository.save(capture(savedTicket)) } answers { issued }

        val result = waitingService.issueTicket(command)

        assertEquals(100, result.id)
        assertEquals(WaitingStatus.WAITING, result.status)
        then { waitingQueueStore.enqueue("concert", 100, 1) }
    }

    @Test
    fun `should reuse existing waiting ticket`() {
        val command = IssueWaitingTicketCommand(queueCode = "concert", memberId = 10)
        val existing =
            WaitingTicket(
                queueCode = command.queueCode,
                memberId = command.memberId,
                issuedSequence = 5,
                expiredAt = Instant.now(clock).plusSeconds(60),
            )
        setId(existing, 55)

        given {
            waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
                command.queueCode,
                command.memberId,
                listOf(WaitingStatus.WAITING),
            )
        } returns Optional.of(existing)

        val result = waitingService.issueTicket(command)

        assertEquals(55, result.id)
        verify { waitingQueueStore wasNot Called }
    }

    @Test
    fun `should expire old ticket and create new one`() {
        val command = IssueWaitingTicketCommand(queueCode = "concert", memberId = 10)
        val existing =
            WaitingTicket(
                queueCode = command.queueCode,
                memberId = command.memberId,
                issuedSequence = 2,
                expiredAt = Instant.now(clock).minusSeconds(1),
            )
        setId(existing, 21)
        given {
            waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
                command.queueCode,
                command.memberId,
                listOf(WaitingStatus.WAITING),
            )
        } returns Optional.of(existing)
        val savedTicket = slot<WaitingTicket>()
        given { waitingTicketRepository.save(capture(savedTicket)) } answers {
            val ticket = savedTicket.captured
            if (ticket.id == 0L) {
                setId(ticket, 22)
            }
            ticket
        }
        given { waitingTicketRepository.findTopByQueueCodeOrderByIssuedSequenceDesc(command.queueCode) } returns existing

        val result = waitingService.issueTicket(command)

        assertEquals(22, result.id)
        verify { waitingQueueStore.remove("concert", 21) }
        verify { waitingQueueStore.enqueue("concert", 22, 3) }
    }

    @Test
    fun `should admit ticket and publish event`() {
        val ticket =
            WaitingTicket(
                queueCode = "concert",
                memberId = 10,
                issuedSequence = 9,
                expiredAt = Instant.now(clock).plusSeconds(600),
            )
        setId(ticket, 200)

        given { waitingTicketRepository.findById(ticket.id) } returns Optional.of(ticket)
        given { waitingTicketRepository.save(ticket) } returns ticket

        val result =
            waitingService.admitTicket(
                AdmitWaitingTicketCommand(
                    ticketId = ticket.id,
                    estimatedSecondsUntilEntry = 30,
                ),
            )

        assertEquals(WaitingStatus.ADMITTED, result.status)
        then { waitingEventPublisher.publish("concert", 10, 9, 30) }
    }

    @Test
    fun `should return queue position`() {
        val ticket =
            WaitingTicket(
                queueCode = "concert",
                memberId = 10,
                issuedSequence = 5,
                expiredAt = Instant.now(clock).plusSeconds(600),
            )
        setId(ticket, 311)

        given {
            waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
                ticket.queueCode,
                ticket.memberId,
                listOf(WaitingStatus.WAITING, WaitingStatus.ADMITTED),
            )
        } returns Optional.of(ticket)
        given { waitingQueueStore.position(ticket.queueCode, ticket.id) } returns 12

        val status =
            waitingService.getStatus(WaitingStatusQuery(queueCode = "concert", memberId = 10))

        assertEquals(12, status.position)
        assertEquals("WAITING", status.status)
    }

    private fun setId(
        ticket: WaitingTicket,
        value: Long,
    ) {
        val field = ticket.javaClass.superclass!!.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(ticket, value)
    }
}

private fun <T> given(block: MockKMatcherScope.() -> T) = every(block)

private fun then(block: MockKVerificationScope.() -> Unit) = verify(verifyBlock = block)
