package minseok.kafkaplayground.waiting.application

import java.time.Clock
import java.time.Duration
import java.time.Instant
import minseok.kafkaplayground.waiting.application.command.AdmitWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.IssueWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.WaitingStatusQuery
import minseok.kafkaplayground.waiting.domain.WaitingStatus
import minseok.kafkaplayground.waiting.domain.WaitingTicket
import minseok.kafkaplayground.waiting.domain.WaitingTicketRepository
import minseok.kafkaplayground.waiting.support.WaitingQueueStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WaitingService(
    private val waitingTicketRepository: WaitingTicketRepository,
    private val waitingQueueStore: WaitingQueueStore,
    private val waitingEventPublisher: WaitingEventPublisher,
    private val clock: Clock,
) {
    private val ticketTtl: Duration = Duration.ofMinutes(15)

    @Transactional
    fun issueTicket(command: IssueWaitingTicketCommand): WaitingTicket {
        val now = Instant.now(clock)
        val activeStatuses = listOf(WaitingStatus.WAITING)
        val existing = waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
            command.queueCode,
            command.memberId,
            activeStatuses,
        )

        if (existing.isPresent) {
            val ticket = existing.get()
            return if (ticket.isExpired(now)) {
                ticket.markExpired()
                waitingQueueStore.remove(ticket.queueCode, ticket.id)
                waitingTicketRepository.save(ticket)
                createNewTicket(command, now)
            } else {
                ticket
            }
        }

        return createNewTicket(command, now)
    }

    @Transactional
    fun admitTicket(command: AdmitWaitingTicketCommand): WaitingTicket {
        val ticket = waitingTicketRepository.findById(command.ticketId)
            .orElseThrow { IllegalArgumentException("waiting ticket not found: ") }
        ticket.markAdmitted()
        waitingQueueStore.remove(ticket.queueCode, ticket.id)
        val saved = waitingTicketRepository.save(ticket)
        waitingEventPublisher.publish(
            queueCode = saved.queueCode,
            memberId = saved.memberId,
            ticketNumber = saved.issuedSequence,
            etaSeconds = command.estimatedSecondsUntilEntry,
        )
        return saved
    }

    @Transactional(readOnly = true)
    fun getStatus(query: WaitingStatusQuery): WaitingTicketStatus {
        val ticket = waitingTicketRepository.findByQueueCodeAndMemberIdAndStatusIn(
            query.queueCode,
            query.memberId,
            listOf(WaitingStatus.WAITING, WaitingStatus.ADMITTED),
        ).orElseThrow { IllegalArgumentException("waiting ticket not found for member ") }

        val position = if (ticket.status == WaitingStatus.WAITING) {
            waitingQueueStore.position(ticket.queueCode, ticket.id)
        } else {
            0
        }

        return WaitingTicketStatus(
            ticketId = ticket.id,
            queueCode = ticket.queueCode,
            memberId = ticket.memberId,
            status = ticket.status.name,
            position = position,
            issuedSequence = ticket.issuedSequence,
        )
    }

    private fun createNewTicket(command: IssueWaitingTicketCommand, now: Instant): WaitingTicket {
        val lastSequence = waitingTicketRepository.findTopByQueueCodeOrderByIssuedSequenceDesc(command.queueCode)?.issuedSequence
            ?: 0L
        val nextSequence = lastSequence + 1
        val ticket = WaitingTicket(
            queueCode = command.queueCode,
            memberId = command.memberId,
            issuedSequence = nextSequence,
            expiredAt = now.plus(ticketTtl),
        )
        val saved = waitingTicketRepository.save(ticket)
        waitingQueueStore.enqueue(command.queueCode, saved.id, saved.issuedSequence)
        return saved
    }
}
