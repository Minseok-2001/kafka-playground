package minseok.kafkaplayground.waiting.application.command

data class WaitingStatusQuery(
    val queueCode: String,
    val memberId: Long,
)
