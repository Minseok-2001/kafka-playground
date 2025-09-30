package minseok.kafkaplayground.payment.application

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.support.KafkaTopics
import minseok.kafkaplayground.common.event.PaymentTransactionEvent
import minseok.kafkaplayground.payment.domain.PaymentTransaction
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(transaction: PaymentTransaction) {
        val message = PaymentTransactionEvent(
            transactionId = transaction.id,
            reservationId = transaction.reservationId,
            status = transaction.status.name,
        )
        val payload = objectMapper.writeValueAsString(message)
        kafkaTemplate.send(KafkaTopics.PAYMENT_TRANSACTION, transaction.id.toString(), payload)
    }
}
