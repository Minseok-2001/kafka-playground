package minseok.kafkaplayground.notification.application

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant
import minseok.kafkaplayground.common.event.NotificationDispatchEvent
import minseok.kafkaplayground.common.support.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class NotificationEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(notificationId: Long, memberId: Long, channel: String, status: String, occurredAt: Instant = Instant.now()) {
        val event = NotificationDispatchEvent(
            notificationId = notificationId,
            memberId = memberId,
            channel = channel,
            status = status,
            occurredAt = occurredAt,
        )
        val payload = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(KafkaTopics.NOTIFICATION_DISPATCH, notificationId.toString(), payload)
    }
}
