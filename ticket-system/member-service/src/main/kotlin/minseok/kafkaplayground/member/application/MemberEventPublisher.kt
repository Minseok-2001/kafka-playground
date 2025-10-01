package minseok.kafkaplayground.member.application

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.event.MemberLifecycleEvent
import minseok.kafkaplayground.common.support.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class MemberEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(
        memberId: Long,
        type: String,
        occurredAt: Instant = Instant.now(),
    ) {
        val event =
            MemberLifecycleEvent(
                memberId = memberId,
                type = type,
                occurredAt = occurredAt,
            )
        val payload = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(KafkaTopics.MEMBER_LIFECYCLE, memberId.toString(), payload)
    }
}
