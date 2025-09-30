package minseok.kafkaplayground.waiting.domain

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface WaitingTicketRepository : JpaRepository<WaitingTicket, Long> {
    fun findTopByQueueCodeOrderByIssuedSequenceDesc(queueCode: String): WaitingTicket?
    fun findByQueueCodeAndMemberIdAndStatusIn(queueCode: String, memberId: Long, statuses: Collection<WaitingStatus>): Optional<WaitingTicket>
}
