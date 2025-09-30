package minseok.kafkaplayground.ticket.domain

import org.springframework.data.jpa.repository.JpaRepository

interface TicketReservationRepository : JpaRepository<TicketReservation, Long> {
    fun findByMemberId(memberId: Long): List<TicketReservation>
}
