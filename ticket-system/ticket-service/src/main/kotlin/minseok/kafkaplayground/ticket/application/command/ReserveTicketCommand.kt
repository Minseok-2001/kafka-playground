package minseok.kafkaplayground.ticket.application.command

data class ReserveTicketCommand(
    val memberId: Long,
    val seatNumber: String,
)
