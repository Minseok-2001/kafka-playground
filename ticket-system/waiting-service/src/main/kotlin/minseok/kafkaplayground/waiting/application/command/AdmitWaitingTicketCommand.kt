package minseok.kafkaplayground.waiting.application.command

data class AdmitWaitingTicketCommand(
    val ticketId: Long,
    val estimatedSecondsUntilEntry: Long,
)
