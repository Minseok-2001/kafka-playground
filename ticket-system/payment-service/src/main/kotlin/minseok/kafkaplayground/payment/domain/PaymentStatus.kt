package minseok.kafkaplayground.payment.domain

enum class PaymentStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    COMPENSATED,
}
