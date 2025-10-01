package minseok.kafkaplayground.ticket.application

import minseok.kafkaplayground.common.event.PaymentTransactionEvent
import minseok.kafkaplayground.ticket.domain.ReservationStatus
import minseok.kafkaplayground.ticket.domain.TicketReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentEventHandler(
    private val ticketReservationRepository: TicketReservationRepository,
) {
    private val logger = LoggerFactory.getLogger(PaymentEventHandler::class.java)

    @Transactional
    fun handle(event: PaymentTransactionEvent) {
        val reservationOptional = ticketReservationRepository.findById(event.reservationId)
        if (reservationOptional.isEmpty) {
            logger.warn("reservation not found for payment event reservationId={}", event.reservationId)
            return
        }

        val reservation = reservationOptional.get()
        val updated =
            when (event.status.uppercase()) {
                ReservationStatus.CONFIRMED.name, "APPROVED" -> {
                    reservation.confirm()
                    true
                }
                ReservationStatus.COMPENSATED.name, "COMPENSATED" -> {
                    reservation.compensate()
                    true
                }
                ReservationStatus.CANCELLED.name, "REJECTED" -> {
                    reservation.cancel()
                    true
                }
                else -> {
                    logger.debug("ignored payment event status={}", event.status)
                    false
                }
            }

        if (updated) {
            ticketReservationRepository.save(reservation)
            logger.info(
                "updated reservation {} to status {} due to payment event {}",
                reservation.id,
                reservation.status,
                event.transactionId,
            )
        }
    }
}
