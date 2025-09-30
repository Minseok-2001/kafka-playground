package minseok.kafkaplayground.payment.application.event

data class PaymentTransactionMessage(
    val transactionId: Long,
    val reservationId: Long,
    val status: String,
)
