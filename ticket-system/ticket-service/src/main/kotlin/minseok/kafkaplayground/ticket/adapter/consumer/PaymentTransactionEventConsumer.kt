package minseok.kafkaplayground.ticket.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.event.PaymentTransactionEvent
import minseok.kafkaplayground.common.support.KafkaTopics
import minseok.kafkaplayground.ticket.application.PaymentEventHandler
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentTransactionEventConsumer(
    private val objectMapper: ObjectMapper,
    private val paymentEventHandler: PaymentEventHandler,
) {
    private val logger = LoggerFactory.getLogger(PaymentTransactionEventConsumer::class.java)

    @KafkaListener(topics = [KafkaTopics.PAYMENT_TRANSACTION], groupId = "ticket-service")
    fun consume(payload: String) {
        val event =
            runCatching { objectMapper.readValue(payload, PaymentTransactionEvent::class.java) }
                .onFailure { throwable ->
                    logger.error("failed to deserialize payment transaction event", throwable)
                }.getOrNull()
                ?: return

        paymentEventHandler.handle(event)
    }
}
