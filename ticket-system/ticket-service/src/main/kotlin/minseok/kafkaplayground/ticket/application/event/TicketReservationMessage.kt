package minseok.kafkaplayground.ticket.application.event

data class TicketReservationMessage(
    val reservationId: Long,
    val memberId: Long,
    val seatNumber: String,
    val status: String,
)
