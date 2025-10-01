package minseok.kafkaplayground.payment.adapter.response

import minseok.kafkaplayground.payment.domain.PaymentTransaction

fun PaymentTransaction.toResponse(): PaymentResponse =
    PaymentResponse(
        id = id,
        reservationId = reservationId,
        amount = amount,
        status = status.name,
    )
