package minseok.kafkaplayground.payment.application

import io.mockk.Runs
import io.mockk.bdd.given
import io.mockk.bdd.then
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import minseok.kafkaplayground.common.BaseEntity
import minseok.kafkaplayground.payment.application.command.RequestPaymentCommand
import minseok.kafkaplayground.payment.domain.PaymentStatus
import minseok.kafkaplayground.payment.domain.PaymentTransaction
import minseok.kafkaplayground.payment.domain.PaymentTransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional

class PaymentServiceTest {
    private val paymentTransactionRepository = mockk<PaymentTransactionRepository>()
    private val paymentEventPublisher = mockk<PaymentEventPublisher>()
    private val paymentService = PaymentService(paymentTransactionRepository, paymentEventPublisher)

    @Test
    fun `requestPayment should persist transaction and publish event`() {
        val command = RequestPaymentCommand(reservationId = 55L, amount = BigDecimal("100.00"))
        val persistedTransaction =
            PaymentTransaction(
                reservationId = command.reservationId,
                amount = command.amount,
            ).withId(9001L)

        val savedSlot = slot<PaymentTransaction>()
        given { paymentTransactionRepository.save(capture(savedSlot)) } answers { persistedTransaction }
        given { paymentEventPublisher.publish(persistedTransaction) } just Runs

        val result = paymentService.requestPayment(command)

        assertThat(result).isSameAs(persistedTransaction)
        assertThat(savedSlot.captured.reservationId).isEqualTo(command.reservationId)
        assertThat(savedSlot.captured.amount).isEqualByComparingTo(command.amount)
        then(exactly = 1) { paymentTransactionRepository.save(any()) }
        then(exactly = 1) { paymentEventPublisher.publish(persistedTransaction) }
    }

    @Test
    fun `markApproved should set status to approved and publish`() {
        val transactionId = 7L
        val transaction = sampleTransaction().withId(transactionId)
        given { paymentTransactionRepository.findById(transactionId) } answers { Optional.of(transaction) }
        given { paymentEventPublisher.publish(transaction) } just Runs

        paymentService.markApproved(transactionId)

        assertThat(transaction.status).isEqualTo(PaymentStatus.APPROVED)
        then(exactly = 1) { paymentEventPublisher.publish(transaction) }
    }

    @Test
    fun `markCompensated should set status to compensated and publish`() {
        val transactionId = 8L
        val transaction = sampleTransaction().withId(transactionId)
        given { paymentTransactionRepository.findById(transactionId) } answers { Optional.of(transaction) }
        given { paymentEventPublisher.publish(transaction) } just Runs

        paymentService.markCompensated(transactionId)

        assertThat(transaction.status).isEqualTo(PaymentStatus.COMPENSATED)
        then(exactly = 1) { paymentEventPublisher.publish(transaction) }
    }

    @Test
    fun `markApproved should throw when transaction not found`() {
        given { paymentTransactionRepository.findById(any()) } answers { Optional.empty<PaymentTransaction>() }

        assertThatThrownBy { paymentService.markApproved(999L) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("payment transaction not found")
    }

    private fun sampleTransaction(): PaymentTransaction =
        PaymentTransaction(
            reservationId = 11L,
            amount = BigDecimal("10.00"),
        )

    private fun PaymentTransaction.withId(id: Long): PaymentTransaction {
        val field = BaseEntity::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(this, id)
        return this
    }
}
