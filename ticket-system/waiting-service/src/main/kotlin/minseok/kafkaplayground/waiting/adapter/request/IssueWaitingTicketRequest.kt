package minseok.kafkaplayground.waiting.adapter.request

import jakarta.validation.constraints.NotNull

class IssueWaitingTicketRequest(
    @field:NotNull
    val memberId: Long,
)
