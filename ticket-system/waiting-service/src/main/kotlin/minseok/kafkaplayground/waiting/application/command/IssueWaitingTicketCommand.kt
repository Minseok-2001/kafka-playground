package minseok.kafkaplayground.waiting.application.command

data class IssueWaitingTicketCommand(
    val queueCode: String,
    val memberId: Long,
)
