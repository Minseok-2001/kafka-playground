package minseok.kafkaplayground.payment.adapter

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import minseok.kafkaplayground.payment.application.PaymentService
import minseok.kafkaplayground.payment.application.RequestPaymentCommand
import minseok.kafkaplayground.payment.domain.PaymentTransaction
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
@Validated
class PaymentController(
    private val paymentService: PaymentService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun requestPayment(@Valid @RequestBody request: PaymentRequest): PaymentResponse {
        val transaction = paymentService.requestPayment(
            RequestPaymentCommand(
                reservationId = request.reservationId,
                amount = request.amount,
            ),
        )
        return transaction.toResponse()
    }

    @PostMapping("/{transactionId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun approve(@PathVariable transactionId: Long) {
        paymentService.markApproved(transactionId)
    }

    @GetMapping
    fun list(): List<PaymentResponse> = paymentService.findAll().map { it.toResponse() }
}

data class PaymentRequest(
    @field:NotNull
    val reservationId: Long,
    @field:DecimalMin("0.0", inclusive = false)
    val amount: BigDecimal,
)

data class PaymentResponse(
    val id: Long,
    val reservationId: Long,
    val amount: BigDecimal,
    val status: String,
)

private fun PaymentTransaction.toResponse(): PaymentResponse = PaymentResponse(
    id = id,
    reservationId = reservationId,
    amount = amount,
    status = status.name,
)
