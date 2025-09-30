package minseok.kafkaplayground.payment.adapter.response

import java.math.BigDecimal

data class PaymentResponse(
    val id: Long,
    val reservationId: Long,
    val amount: BigDecimal,
    val status: String,
)
