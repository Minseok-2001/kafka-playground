package minseok.kafkaplayground.queue.consumer

import minseok.kafkaplayground.common.support.KafkaTopics
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentEventConsumer {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [KafkaTopics.PAYMENT_TRANSACTION], groupId = "queue-service")
    fun handle(payload: String) {
        logger.info { "received payment event -> $payload" }
    }
}
