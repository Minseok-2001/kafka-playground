package minseok.kafkaplayground.payment.application

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.util.Optional
import minseok.kafkaplayground.common.BaseEntity
import minseok.kafkaplayground.payment.application.command.RequestPaymentCommand
import minseok.kafkaplayground.payment.domain.PaymentStatus
import minseok.kafkaplayground.payment.domain.PaymentTransaction
import minseok.kafkaplayground.payment.domain.PaymentTransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PaymentServiceTest {

    private val paymentTransactionRepository = mockk<PaymentTransactionRepository>()
    private val paymentEventPublisher = mockk<PaymentEventPublisher>()
    private val paymentService = PaymentService(paymentTransactionRepository, paymentEventPublisher)

    @Test
    fun `requestPayment should persist transaction and publish event`() {
        val command = RequestPaymentCommand(reservationId = 55L, amount = BigDecimal("100.00"))
        val persistedTransaction = PaymentTransaction(
            reservationId = command.reservationId,
            amount = command.amount,
        ).withId(9001L)

        val savedSlot = slot<PaymentTransaction>()
        every { paymentTransactionRepository.save(capture(savedSlot)) } returns persistedTransaction
        every { paymentEventPublisher.publish(persistedTransaction) } just Runs

        val result = paymentService.requestPayment(command)

        assertThat(result).isSameAs(persistedTransaction)
        assertThat(savedSlot.captured.reservationId).isEqualTo(command.reservationId)
        assertThat(savedSlot.captured.amount).isEqualByComparingTo(command.amount)
        verify(exactly = 1) { paymentTransactionRepository.save(any()) }
        verify(exactly = 1) { paymentEventPublisher.publish(persistedTransaction) }
    }

    @Test
    fun `markApproved should set status to approved and publish`() {
        val transactionId = 7L
        val transaction = sampleTransaction().withId(transactionId)
        every { paymentTransactionRepository.findById(transactionId) } returns Optional.of(transaction)
        every { paymentEventPublisher.publish(transaction) } just Runs

        paymentService.markApproved(transactionId)

        assertThat(transaction.status).isEqualTo(PaymentStatus.APPROVED)
        verify(exactly = 1) { paymentEventPublisher.publish(transaction) }
    }

    @Test
    fun `markCompensated should set status to compensated and publish`() {
        val transactionId = 8L
        val transaction = sampleTransaction().withId(transactionId)
        every { paymentTransactionRepository.findById(transactionId) } returns Optional.of(transaction)
        every { paymentEventPublisher.publish(transaction) } just Runs

        paymentService.markCompensated(transactionId)

        assertThat(transaction.status).isEqualTo(PaymentStatus.COMPENSATED)
        verify(exactly = 1) { paymentEventPublisher.publish(transaction) }
    }

    @Test
    fun `markApproved should throw when transaction not found`() {
        every { paymentTransactionRepository.findById(any()) } returns Optional.empty()

        assertThatThrownBy { paymentService.markApproved(999L) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("payment transaction not found")
    }

    private fun sampleTransaction(): PaymentTransaction {
        return PaymentTransaction(
            reservationId = 11L,
            amount = BigDecimal("10.00"),
        )
    }

    private fun PaymentTransaction.withId(id: Long): PaymentTransaction {
        val field = BaseEntity::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(this, id)
        return this
    }
}
