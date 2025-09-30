package minseok.kafkaplayground.waiting.application

data class WaitingTicketStatus(
    val ticketId: Long,
    val queueCode: String,
    val memberId: Long,
    val status: String,
    val position: Long?,
    val issuedSequence: Long,
)
