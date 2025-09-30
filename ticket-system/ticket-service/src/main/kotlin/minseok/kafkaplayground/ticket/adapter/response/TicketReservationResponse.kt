package minseok.kafkaplayground.ticket.adapter.response

data class TicketReservationResponse(
    val id: Long,
    val memberId: Long,
    val seatNumber: String,
    val status: String,
)
