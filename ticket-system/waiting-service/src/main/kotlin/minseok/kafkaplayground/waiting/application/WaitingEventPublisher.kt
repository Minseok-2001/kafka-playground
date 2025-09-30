package minseok.kafkaplayground.waiting.application

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.event.WaitingAdmissionEvent
import minseok.kafkaplayground.common.support.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class WaitingEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(queueCode: String, memberId: Long, ticketNumber: Long, etaSeconds: Long) {
        val event = WaitingAdmissionEvent(
            queueCode = queueCode,
            memberId = memberId,
            ticketNumber = ticketNumber,
            estimatedSecondsUntilEntry = etaSeconds,
        )
        val payload = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(KafkaTopics.WAITING_ADMISSION, ":", payload)
    }
}
