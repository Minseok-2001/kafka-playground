package minseok.kafkaplayground.payment.application.command

import java.math.BigDecimal

data class RequestPaymentCommand(
    val reservationId: Long,
    val amount: BigDecimal,
)
