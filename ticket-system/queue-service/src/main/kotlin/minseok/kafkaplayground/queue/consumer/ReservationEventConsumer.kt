package minseok.kafkaplayground.queue.consumer

import minseok.kafkaplayground.common.support.KafkaTopics
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ReservationEventConsumer {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [KafkaTopics.TICKET_RESERVATION], groupId = "queue-service")
    fun handle(payload: String) {
        logger.info { "received reservation event -> $payload" }
    }
}
