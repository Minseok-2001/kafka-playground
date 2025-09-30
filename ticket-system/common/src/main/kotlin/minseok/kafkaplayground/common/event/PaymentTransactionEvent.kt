package minseok.kafkaplayground.common.event

data class PaymentTransactionEvent(
    val transactionId: Long,
    val reservationId: Long,
    val status: String,
)
