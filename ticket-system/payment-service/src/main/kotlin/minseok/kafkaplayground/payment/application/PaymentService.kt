package minseok.kafkaplayground.payment.application

import minseok.kafkaplayground.payment.application.command.RequestPaymentCommand
import minseok.kafkaplayground.payment.domain.PaymentTransaction
import minseok.kafkaplayground.payment.domain.PaymentTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentTransactionRepository: PaymentTransactionRepository,
    private val paymentEventPublisher: PaymentEventPublisher,
) {
    @Transactional
    fun requestPayment(command: RequestPaymentCommand): PaymentTransaction {
        val transaction =
            PaymentTransaction(
                reservationId = command.reservationId,
                amount = command.amount,
            )
        val saved = paymentTransactionRepository.save(transaction)
        paymentEventPublisher.publish(saved)
        return saved
    }

    @Transactional
    fun markApproved(transactionId: Long) {
        val transaction =
            paymentTransactionRepository
                .findById(transactionId)
                .orElseThrow { IllegalArgumentException("payment transaction not found: $transactionId") }
        transaction.approve()
        paymentEventPublisher.publish(transaction)
    }

    @Transactional
    fun markCompensated(transactionId: Long) {
        val transaction =
            paymentTransactionRepository
                .findById(transactionId)
                .orElseThrow { IllegalArgumentException("payment transaction not found: $transactionId") }
        transaction.compensate()
        paymentEventPublisher.publish(transaction)
    }

    fun findAll(): List<PaymentTransaction> = paymentTransactionRepository.findAll()
}
