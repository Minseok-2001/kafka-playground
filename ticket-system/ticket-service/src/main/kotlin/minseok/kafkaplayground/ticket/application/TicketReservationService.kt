package minseok.kafkaplayground.ticket.application

import minseok.kafkaplayground.ticket.domain.TicketReservation
import minseok.kafkaplayground.ticket.domain.TicketReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketReservationService(
    private val ticketReservationRepository: TicketReservationRepository,
) {
    @Transactional
    fun reserve(command: ReserveTicketCommand): TicketReservation {
        val reservation = TicketReservation(
            memberId = command.memberId,
            seatNumber = command.seatNumber,
        )
        return ticketReservationRepository.save(reservation)
    }

    fun findByMember(memberId: Long): List<TicketReservation> {
        return ticketReservationRepository.findByMemberId(memberId)
    }
}

data class ReserveTicketCommand(
    val memberId: Long,
    val seatNumber: String,
)
