package minseok.kafkaplayground.ticket.application

import minseok.kafkaplayground.ticket.application.command.ReserveTicketCommand
import minseok.kafkaplayground.ticket.domain.TicketReservation
import minseok.kafkaplayground.ticket.domain.TicketReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketReservationService(
    private val ticketReservationRepository: TicketReservationRepository,
    private val ticketReservationPublisher: TicketReservationPublisher,
) {
    @Transactional
    fun reserve(command: ReserveTicketCommand): TicketReservation {
        val reservation =
            TicketReservation(
                memberId = command.memberId,
                seatNumber = command.seatNumber,
            )
        val saved = ticketReservationRepository.save(reservation)
        ticketReservationPublisher.publishReserved(saved)
        return saved
    }

    fun findByMember(memberId: Long): List<TicketReservation> = ticketReservationRepository.findByMemberId(memberId)
}
