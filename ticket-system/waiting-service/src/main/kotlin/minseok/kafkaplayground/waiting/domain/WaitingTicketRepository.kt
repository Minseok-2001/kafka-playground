package minseok.kafkaplayground.waiting.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface WaitingTicketRepository : JpaRepository<WaitingTicket, Long> {
    fun findTopByQueueCodeOrderByIssuedSequenceDesc(queueCode: String): WaitingTicket?

    fun findByQueueCodeAndMemberIdAndStatusIn(
        queueCode: String,
        memberId: Long,
        statuses: Collection<WaitingStatus>,
    ): Optional<WaitingTicket>
}
