package minseok.kafkaplayground.payment.adapter

import jakarta.validation.Valid
import minseok.kafkaplayground.payment.adapter.request.PaymentRequest
import minseok.kafkaplayground.payment.adapter.response.PaymentResponse
import minseok.kafkaplayground.payment.adapter.response.toResponse
import minseok.kafkaplayground.payment.application.PaymentService
import minseok.kafkaplayground.payment.application.command.RequestPaymentCommand
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
    fun requestPayment(
        @Valid @RequestBody request: PaymentRequest,
    ): PaymentResponse {
        val transaction =
            paymentService.requestPayment(
                RequestPaymentCommand(
                    reservationId = request.reservationId,
                    amount = request.amount,
                ),
            )
        return transaction.toResponse()
    }

    @PostMapping("/{transactionId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun approve(
        @PathVariable transactionId: Long,
    ) {
        paymentService.markApproved(transactionId)
    }

    @GetMapping
    fun list(): List<PaymentResponse> = paymentService.findAll().map { it.toResponse() }
}
