package minseok.kafkaplayground.payment.adapter.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PaymentRequest(
    @field:NotNull
    val reservationId: Long,
    @field:DecimalMin("0.0", inclusive = false)
    val amount: BigDecimal,
)
