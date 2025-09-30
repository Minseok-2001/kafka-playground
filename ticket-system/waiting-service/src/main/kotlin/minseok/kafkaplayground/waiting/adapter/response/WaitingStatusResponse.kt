package minseok.kafkaplayground.waiting.adapter.response

data class WaitingStatusResponse(
    val ticketId: Long,
    val queueCode: String,
    val memberId: Long,
    val status: String,
    val position: Long?,
    val issuedSequence: Long,
)
