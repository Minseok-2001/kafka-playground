package minseok.kafkaplayground.waiting.adapter.response

import java.time.Instant

data class WaitingTicketResponse(
    val id: Long,
    val queueCode: String,
    val memberId: Long,
    val status: String,
    val issuedSequence: Long,
    val expiredAt: Instant,
)
