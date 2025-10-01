package minseok.kafkaplayground.ticket.application

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.support.KafkaTopics
import minseok.kafkaplayground.ticket.application.event.TicketReservationMessage
import minseok.kafkaplayground.ticket.domain.TicketReservation
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class TicketReservationPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publishReserved(reservation: TicketReservation) {
        val message =
            TicketReservationMessage(
                reservationId = reservation.id,
                memberId = reservation.memberId,
                seatNumber = reservation.seatNumber,
                status = reservation.status.name,
            )
        val payload = objectMapper.writeValueAsString(message)
        kafkaTemplate.send(KafkaTopics.TICKET_RESERVATION, reservation.id.toString(), payload)
    }
}
