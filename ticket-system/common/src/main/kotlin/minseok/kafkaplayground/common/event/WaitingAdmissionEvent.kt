package minseok.kafkaplayground.common.event

data class WaitingAdmissionEvent(
    val queueCode: String,
    val memberId: Long,
    val ticketNumber: Long,
    val estimatedSecondsUntilEntry: Long,
)
