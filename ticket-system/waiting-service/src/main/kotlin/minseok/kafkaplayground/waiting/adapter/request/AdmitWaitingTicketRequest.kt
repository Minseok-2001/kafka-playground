package minseok.kafkaplayground.waiting.adapter.request

import jakarta.validation.constraints.Min

class AdmitWaitingTicketRequest(
    @field:Min(0)
    val estimatedSecondsUntilEntry: Long,
)
