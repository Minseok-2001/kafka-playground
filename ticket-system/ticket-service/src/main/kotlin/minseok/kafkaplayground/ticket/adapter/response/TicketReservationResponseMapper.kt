package minseok.kafkaplayground.ticket.adapter.response

import minseok.kafkaplayground.ticket.domain.TicketReservation

fun TicketReservation.toResponse(): TicketReservationResponse = TicketReservationResponse(
    id = id,
    memberId = memberId,
    seatNumber = seatNumber,
    status = status.name,
)
